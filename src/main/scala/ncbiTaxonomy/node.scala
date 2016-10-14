package com.bio4j.data.ncbiTaxonomy

import com.bio4j.model.NCBITaxonomyGraph.TaxonomicRanks

case object DmpFormat {

  val separator = "\t|\t" // or just "|"?

  implicit class Row(val str: String) extends AnyVal {

    def dmpColumns: Seq[String] = str
      .stripSuffix("\t|") // otherwise we will have an extra empty column
      .split(separator)
      .map { _.trim }
  }
}

case class TaxonNode(val line: String) extends AnyVal {
  import DmpFormat._

  def taxID:       String = line.dmpColumns(0)
  def parentTaxID: String = line.dmpColumns(1)
  def rank: TaxonomicRanks = conversions.stringToRank(line.dmpColumns(2))
}

// Only rows with scientific names:
case class ScientificName(val scientificLine: String) extends AnyVal {
  import DmpFormat._

  def taxID: String = scientificLine.dmpColumns(0)
  def scientificName: String = scientificLine.dmpColumns(1)
}


case object Nodes {

  def fromDmpLines(lines: Iterator[String]): Iterator[TaxonNode] = lines.map(TaxonNode)
}

case object Names {
  import DmpFormat._

  def fromDmpLines(lines: Iterator[String]): Iterator[ScientificName] = {
    lines.filter {
      _.dmpColumns(3).trim == "scientific name"
    }.map(ScientificName)
  }
}
