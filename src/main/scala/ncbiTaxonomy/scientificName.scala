package com.bio4j.data.ncbiTaxonomy

// Only rows with scientific names:
case class ScientificName(val scientificLine: String) extends AnyVal {

  def taxID: String = scientificLine.dmpColumns(0)
  def scientificName: String = scientificLine.dmpColumns(1)
}
