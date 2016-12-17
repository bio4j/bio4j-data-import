package com.bio4j.release.generic.uniProtEnzyme

import com.bio4j.model._
import scala.compat.java8.OptionConverters._
import bio4j.data.uniprot._

case class ImportUniProtEnzyme[V,E](val graph: UniProtENZYMEGraph[V,E]) {

  type G = UniProtENZYMEGraph[V,E]
  def g: G = graph

  def enzymeAnnotations(entry: AnyEntry) = {

    // TODO implement sub class id extraction
  }

    //         val ids =
    //           (entry: EntryEnzymeAnnotations).enzymeIDs
    //
    //         val canonicalProtein =
    //           g.uniProtGraph.protein.accession.index.find(entry.accession).asScala
    //
    //         ids.map({ id: EnzymeID =>
    //
    //           if(id.isClass) {
    //
    //             g.enzymeGraph.enzymeClass.id.index.find(id.value).asScala flatMap { clazz =>
    //               canonicalProtein map { p =>
    //
    //                 g.enzymeClass.addEdge(p, clazz)
    //               }
    //             }
    //           }
    //           else if(id.isSubClass) {
    //
    //             g.enzymeGraph.enzymeSubClass.id.index.find(id.value).asScala flatMap { subClass =>
    //               canonicalProtein map { p =>
    //
    //                 g.enzymeSubClass.addEdge(p, subClass)
    //               }
    //             }
    //           }
    //           else if(id.isSubSubClass) {
    //
    //             g.enzymeGraph.enzymeSubSubClass.id.index.find(id.value).asScala flatMap { subSubClass =>
    //               canonicalProtein map { p =>
    //
    //                 g.enzymeSubSubClass.addEdge(p, subSubClass)
    //               }
    //             }
    //           }
    //           else {
    //
    //             g.enzymeGraph.enzyme.id.index.find(id.value).asScala flatMap { enzyme =>
    //               canonicalProtein map { p =>
    //
    //                 g.enzyme.addEdge(p, enzyme)
    //               }
    //             }
    //           }
    //         }).flatten
    //       }
    //     )
    // )
}
