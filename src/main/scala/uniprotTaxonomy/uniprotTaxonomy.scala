package com.bio4j.release.generic.uniprotTaxonomy

import bio4j.data.uniprot._
import com.bio4j.model._
import scala.compat.java8.OptionConverters._

case class ImportUniprotTaxonomyAnnotations[V,E](val graph: UniProtNCBITaxonomyGraph[V,E]) {

  type G = UniProtNCBITaxonomyGraph[V,E]
  def g: G = graph

  def taxonomyAnnotations(entry: AnyEntry) =
    findProtein(entry.accessionNumbers.primary) flatMap { protein =>


    val organismEdge =
      findTaxon(entry.taxonomyCrossReference.taxonID) map {
        g.organism.addEdge(protein, _)
      }

    val hosts =
      entry.organismHost.map(
        crossRef => g.ncbiTaxonomyGraph.taxon.id.index.find(crossRef.taxonID).asScala
      )
      .flatten

    val hostEdges =
      entry.organismHost
        .collect(scala.Function.unlift { crossRef => findTaxon(crossRef.taxonID) })
        .map { g.host.addEdge(protein,_) }

    for (e <- organismEdge) yield (e,hostEdges)
  }

  private def findProtein(accession: String) =
    g.uniProtGraph.protein.accession.index.find(accession).asScala

  private def findTaxon(taxonID: String) =
    g.ncbiTaxonomyGraph.taxon.id.index.find(taxonID).asScala

}
