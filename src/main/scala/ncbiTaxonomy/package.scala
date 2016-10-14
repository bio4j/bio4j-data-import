package com.bio4j.data


package object ncbiTaxonomy {

  val dmpSeparator = '|'


  implicit class DmpRow(val str: String) extends AnyVal {

    def dmpColumns: Seq[String] = str
      .stripSuffix("\t|") // otherwise we will have an extra empty column
      .split(dmpSeparator)
      .map { _.trim }
  }


  implicit class DmpIterator(val lines: Iterator[String]) extends AnyVal {

    def nodes: Iterator[TaxonNode] = lines.map(TaxonNode)

    def scientificNames: Iterator[ScientificName] = {
      lines.filter {
        _.dmpColumns(3).trim == "scientific name"
      }.map(ScientificName)
    }
  }
}
