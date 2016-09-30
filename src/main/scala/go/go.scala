package com.bio4j.data.go

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.xml._
import scala.compat.java8.OptionConverters._

case class ImportProcess[V,E](val graph: GOGraph[V,E]) {

  type G = GOGraph[V,E]

  val terms =
    GraphProcess.generically[V,E] (
      graph,
      (ontology: Obo, g: G) =>
        (
          graph,
          ontology.terms.map { xmlTerm =>
            g.term.addVertex
              .set(g.term.id, xmlTerm.ID)
              .set(g.term.name, xmlTerm.name)
              .set(g.term.definition, xmlTerm.definition)
              .set(g.term.subOntology, toSubontologies(xmlTerm.namespace))
          }
        )
    )

  val isA =
    GraphProcess.generically[V,E] (
      graph,
      (ontology: Obo, g: G) =>
        (
          graph,
          ontology.isA.map( rel =>
            sourceAndTarget(rel) map { case (source,target) =>
              g.isA.addEdge(source, target)
            }
          )
          .flatten
        )
    )

  val partOf =
    GraphProcess.generically[V,E] (
      graph,
      (ontology: Obo, g: G) =>
        (
          graph,
          ontology.partOf.map( rel =>
            sourceAndTarget(rel) map { case (source,target) =>
              g.partOf.addEdge(source, target)
            }
          )
          .flatten
        )
    )

  val regulates =
    GraphProcess.generically[V,E] (
      graph,
      (ontology: Obo, g: G) =>
        (
          graph,
          ontology.regulates.map( rel =>
            sourceAndTarget(rel) map { case (source,target) =>
              g.regulates.addEdge(source, target)
            }
          )
          .flatten
        )
    )

  val positivelyRegulates =
    GraphProcess.generically[V,E] (
      graph,
      (ontology: Obo, g: G) =>
        (
          graph,
          ontology.positivelyRegulates.map( rel =>
            sourceAndTarget(rel) map { case (source,target) =>
              g.positivelyRegulates.addEdge(source, target)
            }
          )
          .flatten
        )
    )


  val negativelyRegulates =
    GraphProcess.generically[V,E] (
      graph,
      (ontology: Obo, g: G) =>
        (
          graph,
          ontology.negativelyRegulates.map( rel =>
            sourceAndTarget(rel) map { case (source,target) =>
              g.negativelyRegulates.addEdge(source, target)
            }
          )
          .flatten
        )
    )


  private def termByID(id: String) =
    graph.term.id.index.find(id).asScala

  private def sourceAndTarget(rel: Rel): Option[(G#Term, G#Term)] =
    termByID(rel.sourceID).flatMap { source =>
      termByID(rel.targetID).map { target =>
        (source, target)
      }
    }

  private def toSubontologies(namespace: String) =
    namespace match {

      case "cellular_component" => GOGraph.Subontologies.cellularComponent
      case "biological_process" => GOGraph.Subontologies.biologicalProcess
      case "molecular_function" => GOGraph.Subontologies.molecularFunction
    }
}
