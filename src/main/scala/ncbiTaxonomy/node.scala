// package com.bio4j.data.ncbiTaxonomy
//
// import com.bio4j.model.NCBITaxonomyGraph.TaxonomicRanks
//
// case class TaxonNode(val line: String) extends AnyVal {
//
//   def taxID:       String = line.dmpColumns(0)
//   def parentTaxID: String = line.dmpColumns(1)
//   def rank: TaxonomicRanks = conversions.stringToRank(line.dmpColumns(2))
// }
