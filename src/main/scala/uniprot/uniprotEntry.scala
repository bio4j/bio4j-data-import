package com.bio4j.data.uniprot

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.xml._
import scala.compat.java8.OptionConverters._

case class Entry(val entry: Elem) extends AnyVal {

  def proteinFullName: String = {

    lazy val recommendedName  = entry \ "protein" \ "recommendedName" \ "fullName" text
    lazy val submittedName    = entry \ "protein" \ "submittedName" \ "fullName" text

    if (recommendedName.isEmpty) submittedName else recommendedName
  }

  def accession: String =
    entry \ "accession" text

  def dataset: UniProtGraph.Datasets =
    entry \ "@dataset" text match {

      case "Swiss-Prot" => UniProtGraph.Datasets.swissProt
      case "TrEMBL"     => UniProtGraph.Datasets.trEMBL
      case _            => UniProtGraph.Datasets.trEMBL
    }

  def existence: UniProtGraph.ExistenceEvidence =
    entry \ "proteinExistence" \ "@type" text match {

      case "predicted"                    => UniProtGraph.ExistenceEvidence.predicted
      case "inferred from homology"       => UniProtGraph.ExistenceEvidence.homologyInferred
      case "evidence at transcript level" => UniProtGraph.ExistenceEvidence.transcriptLevel
      case "evidence at protein level"    => UniProtGraph.ExistenceEvidence.proteinLevel
      case "uncertain"                    => UniProtGraph.ExistenceEvidence.uncertain
      case _                              => UniProtGraph.ExistenceEvidence.predicted // default case
    }

  def sequence: String =
    entry \ "sequence" text

  def length: Int =
    (entry \ "sequence" \ "@length" text).toInt

  def geneName: Option[String] = {

    val names = entry \ "gene" \ "name"

    lazy val primaryNames =
      names filter { elem => (elem \ "@type" text) == "primary" } map { _.text }

    lazy val orfNames =
      names filter { elem => (elem \ "@type" text) == "ORF" } map { _.text }

    /* If there's a primary name, we pick it; otherwise the first ORF name, if any */
    primaryNames.headOption.fold(orfNames.headOption){ Some(_) }
  }

  def comments: Seq[(UniProtGraph.CommentTopics, String)] =
    entry \ "comment" map {
      elem => ( conversions.stringToCommentTopic( elem \ "@type" text ), elem \ "text" text )
    }
}

case object Entry {

  implicit def asXML: Entry => Elem = _.entry
}
