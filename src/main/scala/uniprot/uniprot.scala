package com.bio4j.data.uniprot

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.xml._
import scala.compat.java8.OptionConverters._

// from keywords-all.tsv
case class KeywordRow(val id: String, val description: String, val category: String)

case class CanonicalProtein[V,E](val graph: UniProtGraph[V,E]) {

  type G = UniProtGraph[V,E]

  val fromEntry = AddVertex.generically[V,E](
    graph,
    graph.protein,
    (entry: Entry, g: UniProtGraph[V,E]) =>
      Seq (
        g.protein.addVertex
        .set( g.protein.isCanonical,  true: java.lang.Boolean   )
        .set( g.protein.accession,    entry.accession           )
        .set( g.protein.fullName,     entry.proteinFullName     )
        .set( g.protein.existence,    entry.existence           )
        .set( g.protein.dataset,      entry.dataset             )
        .set( g.protein.sequence,     entry.sequence            )
      )
    )
}

case class GeneName[V,E](val graph: UniProtGraph[V,E]) {

  val fromEntry = AddVertex.generically[V,E](
    graph,
    graph.geneName,
    (entry: Entry, g: UniProtGraph[V,E]) =>
      entry.geneName
        .fold( Seq[UniProtGraph[V,E]#GeneName]() ) {
          name =>
            g.geneName.name.index.find(name).asScala
              .fold(
                Seq(
                  g.geneName.addVertex
                    .set(g.geneName.name, name)
                )
              ){
                Seq(_)
              }
        }
  )
}

case class Comment[V,E](val graph: UniProtGraph[V,E]) {

  val fromEntry = AddVertex.generically[V,E](
    graph,
    graph.comment,
    (entry: Entry, g: UniProtGraph[V,E]) => {

      val comments =
        entry.comments map {
          case (topic, text) =>
            g.comment.addVertex
              .set(g.comment.topic, topic)
              .set(g.comment.text, text)
        }

      // add edges from proteins to these comments; I'm forced to do this here.
      val proteinOpt =
        graph.protein.accession.index.find( entry.accession ).asScala

      proteinOpt.foreach { protein =>
        comments.foreach { comment =>
          g.comments.addEdge( protein, comment )
        }
      }

      comments
    }
  )
}

case class Keyword[V,E](val graph: UniProtGraph[V,E]) {

  val fromRow = AddVertex.generically[V,E](
    graph,
    graph.keyword,
    (row: KeywordRow, g: UniProtGraph[V,E]) =>
      Seq(
        g.keyword.addVertex
          .set(g.keyword.name, row.id)
          .set(g.keyword.definition, row.description)
          .set(g.keyword.category, conversions.stringToKeywordCategory(row.category))
      )
  )
}

case class Annotation[V,E](val graph: UniProtGraph[V,E]) {

  val fromEntry = AddVertex.generically[V,E](
    graph,
    graph.annotation,
    (entry: Entry, g: UniProtGraph[V,E]) => {

      val proteinOpt =
        graph.protein.accession.index.find( entry.accession ).asScala

      entry.features map {
        case (featureType, maybeDescription, uniProtLocation) =>
          val annotation =
            g.annotation.addVertex

          maybeDescription.foreach { annotation.set(g.annotation.description, _) }

          val location = ZeroHalfOpenLocation.fromUniProtLocation(uniProtLocation)

          // again forced to create this edge along the way
          proteinOpt.foreach { protein =>
            g.annotations.addEdge(protein, annotation)
              .set(g.annotations.begin, location.begin: java.lang.Integer)
              .set(g.annotations.end, location.end: java.lang.Integer)
          }
        annotation
      }
    }
  )
}

case class Isoform[V,E](val graph: UniProtGraph[V,E]) {

  val fromEntry = AddVertex.generically[V,E](
    graph,
    graph.protein,
    (entry: Entry, g: UniProtGraph[V,E]) => {

      val maybeEntryProtein = g.protein.accession.index.find(entry.accession).asScala

      entry.isoforms map {
        case (id, name) => {

          // TODO review this. Shall we use the isoform id instead of accessions everywhere?
          g.protein.accession.index.find(id).asScala.fold(
            { // need to add the new isoform
              val isoform = g.protein.addVertex
                .set(g.protein.accession, id)
                .set(g.protein.fullName, name)
                .set(g.protein.isCanonical, false: java.lang.Boolean) // critical that this goes *after* loading "proteins"

              maybeEntryProtein.foreach { protein => g.isoforms.addEdge(protein, isoform) }

              isoform
            }
          ){ // already there; add an edge from the current entry protein
            isoform =>
              maybeEntryProtein.foreach { protein => g.isoforms.addEdge(protein, isoform) }
              isoform
          }
        }
      }
    }
  )
}
