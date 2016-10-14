package com.bio4j.data.ncbiTaxonomy

import com.bio4j.model.NCBITaxonomyGraph.TaxonomicRanks

case object DmpFormat {

  val separator = "\t|\t" // or just "|"?

  implicit class Row(val str: String) extends AnyVal {

    def columns: Seq[String] = str
      .stripSuffix("\t|")
      .split(separator)
      .map { _.trim }
  }
}

case class Node(val line: String) extends AnyVal {
  import DmpFormat._

  def taxID:       String = line.columns(0)
  def parentTaxID: String = line.columns(1)
  def rank: TaxonomicRanks = conversions.stringToRank(line.columns(2))
}

// Only rows with scientific names:
// file"names.dmp".lines.filter {
//   _.split("\t|\t").apply(3).trim == "scientific name"
// }
case class Name(val scientificLine: String) extends AnyVal {
  import DmpFormat._

  def taxID: String = scientificLine.columns(0)
  def scientificName: String = scientificLine.columns(1)
}
