package com.bio4j.data.uniprotTaxonomy

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.xml._
import scala.compat.java8.OptionConverters._

case class Process[V,E](val graph: UniProtNCBITaxonomyGraph[V,E]) {

  type G = UniProtNCBITaxonomyGraph[V,E]

  val taxonomyAnnotations =
    GraphProcess.generically[V,E] (
      graph,
      (entry: EntryTaxonomyAnnotations, g: G) =>
        (
          graph,
          {

            val canonicalProtein =
              g.uniProtGraph.protein.id.index.find(entry.accession).asScala

            val organism =
              g.ncbiTaxonomyGraph.taxon.id.index.find(entry.organism).asScala

            val organismEdge = canonicalProtein.foreach { p =>
              organism.foreach { t =>
                g.organism.addEdge(p, t)
              }
            }

            val hosts =
              entry.hosts.map(
                host => g.ncbiTaxonomyGraph.taxon.id.index.find(host).asScala
              )
              .flatten

            val hostEdges =
              hosts.foreach { h =>
                canonicalProtein.foreach { p =>
                  g.host.addEdge(p,h)
                }
              }

            (organismEdge, hostEdges)
          }
        )
    )
}
