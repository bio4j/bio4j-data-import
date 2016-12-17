package com.bio4j.release.generic.go

import com.bio4j.data.go._
import com.bio4j.model._
import scala.compat.java8.OptionConverters._

case class ImportGO[V,E](val graph: GOGraph[V,E]) {

  type G = GOGraph[V,E]
  def g: G = graph

  def terms(ontology: AnyGO) =
    ontology.terms.map { term =>
      g.term.addVertex
        .set(g.term.id, term.ID)
        .set(g.term.name, term.name)
        .set(g.term.definition, term.definition)
        .set(g.term.subOntology, toSubontologies(term.namespace))
    }

  def isA(ontology: AnyGO): Seq[G#IsA] =
    sourceAndTargets( ontology.isA ) map {
      case (source,target) => g.isA.addEdge(source, target)
    }

  def partOf(ontology: AnyGO): Seq[G#PartOf] =
    sourceAndTargets( ontology.partOf ) map {
      case (source,target) => g.partOf.addEdge(source, target)
    }

  def regulates(ontology: AnyGO): Seq[G#Regulates] =
    sourceAndTargets( ontology.regulates ) map {
      case (source,target) => g.regulates.addEdge(source, target)
    }

  def positivelyRegulates(ontology: AnyGO): Seq[G#PositivelyRegulates] =
    sourceAndTargets( ontology.positivelyRegulates ) map {
      case (source,target) => g.positivelyRegulates.addEdge(source, target)
    }

  def negativelyRegulates(ontology: AnyGO): Seq[G#NegativelyRegulates] =
    sourceAndTargets( ontology.negativelyRegulates ) map {
      case (source,target) => g.negativelyRegulates.addEdge(source, target)
    }

  private def termByID(id: String) =
    g.term.id.index.find(id).asScala

  private def sourceAndTarget(rel: AnyRel): Option[(G#Term, G#Term)] =
    termByID(rel.sourceID).flatMap { source =>
      termByID(rel.targetID).map { target =>
        (source, target)
      }
    }

  private def sourceAndTargets(rels: Seq[AnyRel]): Seq[(G#Term, G#Term)] =
    rels collect Function.unlift(sourceAndTarget)

  private def toSubontologies(namespace: Namespace) =
    namespace match {
      case `cellular_component` => GOGraph.Subontologies.cellularComponent
      case `biological_process` => GOGraph.Subontologies.biologicalProcess
      case `molecular_function` => GOGraph.Subontologies.molecularFunction
    }
}
