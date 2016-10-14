package com.bio4j.data.ncbiTaxonomy

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.compat.java8.OptionConverters._

case class Process[V,E](val graph: NCBITaxonomyGraph[V,E]) {

  type G = NCBITaxonomyGraph[V,E]

  val nodes = GraphProcess.generically[V,E] (graph,
    (node: TaxonNode, g: G) => {

      val vertex: G#Taxon = g.taxon.addVertex
        .set(g.taxon.id, node.taxID)
        .set(g.taxon.taxonomicRank, node.rank)

      (graph, vertex)
    }
  )

  val parents = GraphProcess.generically[V,E] (graph,
    (node: TaxonNode, g: G) => {

      val edge: Option[G#Parent] =
        // NOTE: root has same ID for its parent, but it shouldn't have a cyclic edge
        if (node.taxID == node.parentTaxID) None
        else {
          val srcOpt = g.taxon.id.index.find(node.taxID).asScala
          val tgtOpt = g.taxon.id.index.find(node.parentTaxID).asScala

          (srcOpt zip tgtOpt).headOption.map { case (s, t) =>
            g.parent.addEdge(s, t)
          }
        }

      (graph, edge)
    }
  )

  val names = GraphProcess.generically[V,E] (graph,
    (taxName: ScientificName, g: G) => {

      val taxon: Option[G#Taxon] = g.taxon.id.index.find(taxName.taxID).asScala

      val vertex: Option[G#Taxon] = taxon.map {
        _.set(g.taxon.name, taxName.scientificName)
      }

      (graph, vertex)
    }
  )
}
