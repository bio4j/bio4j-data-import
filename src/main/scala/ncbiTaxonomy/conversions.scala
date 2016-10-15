package com.bio4j.data.ncbiTaxonomy

import com.bio4j.model._, NCBITaxonomyGraph.TaxonomicRanks
import com.bio4j.angulillos._
import scala.xml._
import scala.compat.java8.OptionConverters._


case object conversions {

  val ranksMap: Map[String, TaxonomicRanks] = Map(
    "superkingdom"    -> TaxonomicRanks.superkingdom,
    "kingdom"         -> TaxonomicRanks.kingdom,
    "superphylum"     -> TaxonomicRanks.superphylum,
    "phylum"          -> TaxonomicRanks.phylum,
    "subphylum"       -> TaxonomicRanks.subphylum,
    "clazz"           -> TaxonomicRanks.clazz, // reserved word
    "subclass"        -> TaxonomicRanks.subclass,
    "superclass"      -> TaxonomicRanks.superclass,
    "infraclass"      -> TaxonomicRanks.infraclass,
    "order"           -> TaxonomicRanks.order,
    "parvorder"       -> TaxonomicRanks.parvorder,
    "suborder"        -> TaxonomicRanks.suborder,
    "infraorder"      -> TaxonomicRanks.infraorder,
    "family"          -> TaxonomicRanks.family,
    "subfamily"       -> TaxonomicRanks.subfamily,
    "superfamily"     -> TaxonomicRanks.superfamily,
    "tribe"           -> TaxonomicRanks.tribe,
    "subtribe"        -> TaxonomicRanks.subtribe,
    "genus"           -> TaxonomicRanks.genus,
    "subgenus"        -> TaxonomicRanks.subgenus,
    "speciesGroup"    -> TaxonomicRanks.speciesGroup,
    "speciesSubgroup" -> TaxonomicRanks.speciesSubgroup,
    "species"         -> TaxonomicRanks.species,
    "subspecies"      -> TaxonomicRanks.subspecies,
    "varietas"        -> TaxonomicRanks.varietas,
    "forma"           -> TaxonomicRanks.forma,
    "no rank"         -> TaxonomicRanks.noRank
  )

  def stringToRank(str: String): TaxonomicRanks =
    ranksMap.get(str).getOrElse(
      TaxonomicRanks.thereIsAnIndeterminateNumberOfRanksAsATaxonomistMayInventANewRankAtWillAtAnyTimeIfTheyFeelThisIsNecessary
    )
}
