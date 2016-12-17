package com.bio4j.release.generic.uniProtGO

import bio4j.data.uniprot._
import com.bio4j.model._
import scala.compat.java8.OptionConverters._

case class ImportUniProtGO[V,E](val graph: UniProtGOGraph[V,E]) {

  type G = UniProtGOGraph[V,E]
  def g: G = graph

  def goAnnotations(entry: AnyEntry) = {

      val goTermIDs =
        entry.databaseCrossReferences.collect {
          case DatabaseCrossReference(resource, id) =>
            resource match {
              case GO => id
            }
        }

      findProtein(entry.accessionNumbers.primary) map { protein =>

        goTermIDs.collect { scala.Function.unlift { termID =>
            findTerm(termID) map { g.annotation.addEdge(protein, _) }
          }
        }
      }
    }

  private def findTerm(id: String) =
    g.goGraph.term.id.index.find(id).asScala

  private def findProtein(accession: String) =
    g.uniProtGraph.protein.accession.index.find(accession).asScala
}
