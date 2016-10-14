package com.bio4j.data.ncbiTaxonomy

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.compat.java8.OptionConverters._

case class Process[V,E](val graph: NCBITaxonomyGraph[V,E]) {

  type G = NCBITaxonomyGraph[V,E]

  val nodes = GraphProcess.generically[V,E] (graph,
    (node: TaxonNode, g: G) => {

      val vertex = g.taxon.addVertex
        .set(g.taxon.id, node.taxID)
        .set(g.taxon.taxonomicRank, node.rank)

      (graph, vertex)
    }
  )

  val parents = GraphProcess.generically[V,E] (graph,
    (node: TaxonNode, g: G) => {

      val srcOpt = g.taxon.id.index.find(node.taxID).asScala
      val tgtOpt = g.taxon.id.index.find(node.parentTaxID).asScala

      val edges = (srcOpt zip tgtOpt).map { case (s, t) =>
        g.parent.addEdge(s, t)
      }

      (graph, edges)
    }
  )

  val names = GraphProcess.generically[V,E] (graph,
    (taxName: ScientificName, g: G) => {

      val taxon = g.taxon.id.index.find(taxName.taxID).asScala

      val vertices = taxon.map {
        _.set(g.taxon.name, taxName.scientificName)
      }

      (graph, vertices)
    }
  )

  private def findNode(g: G, id: String): Option[G#Taxon] =
    g.taxon.id.index.find(id).asScala
}
