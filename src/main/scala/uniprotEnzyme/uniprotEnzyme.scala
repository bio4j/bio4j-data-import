package com.bio4j.data.uniprotEnzyme

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.compat.java8.OptionConverters._
import uniprot.Entry
import enzyme.EnzymeID

case class Process[V,E](val graph: UniProtENZYMEGraph[V,E]) {

  type G = UniProtENZYMEGraph[V,E]

  val goAnnotations =
    GraphProcess.generically[V,E] (
      graph,
      (entry: Entry, g: G) =>
        (
          graph,
          {

            val ids =
              (entry: EntryEnzymeAnnotations).enzymeIDs

            val canonicalProtein =
              g.uniProtGraph.protein.accession.index.find(entry.accession).asScala

            ids.map({ id: EnzymeID =>

              if(id.isClass) {

                g.enzymeGraph.enzymeClass.id.index.find(id.value).asScala flatMap { clazz =>
                  canonicalProtein map { p =>

                    g.enzymeClass.addEdge(p, clazz)
                  }
                }
              }
              else if(id.isSubClass) {

                g.enzymeGraph.enzymeSubClass.id.index.find(id.value).asScala flatMap { subClass =>
                  canonicalProtein map { p =>

                    g.enzymeSubClass.addEdge(p, subClass)
                  }
                }
              }
              else if(id.isSubSubClass) {

                g.enzymeGraph.enzymeSubSubClass.id.index.find(id.value).asScala flatMap { subSubClass =>
                  canonicalProtein map { p =>

                    g.enzymeSubSubClass.addEdge(p, subSubClass)
                  }
                }
              }
              else {

                g.enzymeGraph.enzyme.id.index.find(id.value).asScala flatMap { enzyme =>
                  canonicalProtein map { p =>

                    g.enzyme.addEdge(p, enzyme)
                  }
                }
              }
            }).flatten
          }
        )
    )
}
