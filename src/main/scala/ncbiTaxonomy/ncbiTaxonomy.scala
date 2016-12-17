package com.bio4j.release.generic.ncbiTaxonomy

import com.bio4j.release.generic._
import com.bio4j.data.ncbitaxonomy._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.compat.java8.OptionConverters._

case class ImportNCBITaxonomy[V,E](val graph: NCBITaxonomyGraph[V,E]) {

  // convenience aliases
  type G = NCBITaxonomyGraph[V,E]
  def g: G = graph

  def taxon(node: AnyNode, g: G): G#Taxon = {

    val vertex: G#Taxon =
      g.taxon.addVertex
        .set(g.taxon.id, node.ID)

    NCBITaxonomyGraph.TaxonomicRanks.fromString(node.rank)
      .asScala.foreach {
        vertex.set(g.taxon.rank, _)
      }

    vertex
  }

  def parent(node: AnyNode): Option[G#Parent] =
    // NOTE: root has same ID for its parent, but it shouldn't have a cyclic edge
    if (node.ID == node.parentID) None else {

      val srcOpt = g.taxon.id.index.find(node.ID).asScala
      val tgtOpt = g.taxon.id.index.find(node.parentID).asScala

      (srcOpt zip tgtOpt).headOption.map { case (s, t) =>
        g.parent.addEdge(s, t)
      }
    }

  def name(taxName: AnyNodeName): Option[G#Taxon] =
    g.taxon.id.index.find(taxName.nodeID).asScala
      .map { _.set(g.taxon.name, taxName.name) }
}
