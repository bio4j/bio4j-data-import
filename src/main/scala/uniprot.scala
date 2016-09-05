package com.bio4j.data.uniprot

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.xml._
import scala.compat.java8.OptionConverters._

case class Entry(val xml: Elem) extends AnyVal
case object Entry {

  implicit def asXML: Entry => Elem = _.xml
}

case class CanonicalProtein[V,E](val graph: UniProtGraph[V,E]) {

  type G = UniProtGraph[V,E]

  val fromEntry = AddVertex.generically[V,E](
    graph,
    graph.protein,
    (entry: Entry, g: UniProtGraph[V,E]) =>
      Seq (
        g.protein.addVertex
        .set( g.protein.isCanonical,  true: java.lang.Boolean )
        .set( g.protein.accession,    accessionFromEntry(entry) )
        .set( g.protein.fullName,     fullNameFromEntry(entry) )
        .set( g.protein.existence,    existenceFromEntry(entry) )
        .set( g.protein.dataset,      datasetFromEntry(entry) )
        .set( g.protein.sequence,     sequenceFromEntry(entry) )
      )
    )

  val fullNameFromEntry: Entry => String =
    { entry: Entry =>

      lazy val recommendedName  = entry \ "protein" \ "recommendedName" \ "fullName" text
      lazy val submittedName    = entry \ "protein" \ "submittedName" \ "fullName" text

      if (recommendedName.isEmpty) submittedName else recommendedName
    }

  val accessionFromEntry: Entry => String =
    _ \ "accession" text

  val datasetFromEntry: Entry => UniProtGraph.Datasets =
    _ \ "@dataset" text match {

      case "Swiss-Prot" => UniProtGraph.Datasets.swissProt
      case "TrEMBL"     => UniProtGraph.Datasets.trEMBL
      case _            => UniProtGraph.Datasets.trEMBL
    }

  val existenceFromEntry: Entry => UniProtGraph.ExistenceEvidence =
    _ \ "proteinExistence" \ "@type" text match {

      case "predicted"                    => UniProtGraph.ExistenceEvidence.predicted
      case "inferred from homology"       => UniProtGraph.ExistenceEvidence.homologyInferred
      case "evidence at transcript level" => UniProtGraph.ExistenceEvidence.transcriptLevel
      case "evidence at protein level"    => UniProtGraph.ExistenceEvidence.proteinLevel
      case "uncertain"                    => UniProtGraph.ExistenceEvidence.uncertain
      case _                              => UniProtGraph.ExistenceEvidence.predicted // default case
    }

  val sequenceFromEntry: Entry => String =
    _ \ "sequence" text

  val lengthFromEntry: Entry => Int =
    entry => (entry \ "sequence" \ "@length" text).toInt
}

case class GeneName[V,E](val graph: UniProtGraph[V,E]) {

  val fromEntry = AddVertex.generically[V,E](
    graph,
    graph.geneName,
    (entry: Entry, g: UniProtGraph[V,E]) =>
      nameFromEntry(entry)
        .fold( Seq[UniProtGraph[V,E]#GeneName]() ) {
          _ => Seq(g.geneName.addVertex) // TODO add gene name property; missing in bio4j/bio4j
        }
  )

  val nameFromEntry: Entry => Option[String] =
    { entry =>

      val names = entry \ "gene" \ "name"

      lazy val primaryNames =
        names filter { elem => (elem \ "@type" text) == "primary" } map { _.text }

      lazy val orfNames =
        names filter { elem => (elem \ "@type" text) == "ORF" } map { _.text }

      /* If there's a primary name, we pick it; otherwise the first ORF name, if any */
      primaryNames.headOption.fold(orfNames.headOption){ Some(_) }
    }
}

case class Comment[V,E](val graph: UniProtGraph[V,E]) {

  val fromEntry = AddVertex.generically[V,E](
    graph,
    graph.comment,
    (entry: Entry, g: UniProtGraph[V,E]) => {

      val comments =
        commentTopicAndTextFromEntry(entry) map {
          case (topic, text) =>
            g.comment.addVertex
              .set(g.comment.topic, topic)
              .set(g.comment.text, text)
        }

      // add edges from proteins to these comments
      val proteinOpt =
        graph.protein.accession.index.find( entry \ "accession" text ).asScala

      // TODO no comments edge in bio4j/bio4j
      // proteinOpt.foreach { protein =>
      //   comments.foreach { comment =>
      //     g.comments.addEdge( protein, comment )
      //   }
      // }

      comments
    }
  )

  val commentTopicAndTextFromEntry: Entry => Seq[(UniProtGraph.CommentTopics, String)] =
    _ \ "comment" map { elem => ( stringToCommentTopic( elem \ "@type" text ), elem \ "text" text ) }

  val stringToCommentTopic: String => UniProtGraph.CommentTopics =
    ??? // TODO match big enum
}
