package com.bio4j.data


package object ncbiTaxonomy {

  val dmpSeparator = "\t|\t" // or just "|"?


  implicit class DmpRow(val str: String) extends AnyVal {

    def dmpColumns: Seq[String] = str
      .stripSuffix("\t|") // otherwise we will have an extra empty column
      .split(dmpSeparator)
      .map { _.trim }
  }


  def nodesFromDmp(lines: Iterator[String]): Iterator[TaxonNode] =
    lines.map(TaxonNode)

  def namesFromDmp(lines: Iterator[String]): Iterator[ScientificName] = {
    lines.filter {
      _.dmpColumns(3).trim == "scientific name"
    }.map(ScientificName)
  }
}
