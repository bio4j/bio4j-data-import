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

  /*
    The output here is (type, description, positionInfo).
  */
  def features: Seq[(UniProtGraph.FeatureTypes, Option[String], UniProtLocation)] =
    {

      def parseLocation(location: Node): UniProtLocation =
        (location \ "position").headOption.fold(
          UniProtLocation(
            begin = (location \ "begin" \ "@position" text).toInt,
            end   = (location \ "end" \ "@position" text).toInt
          )
        ){
          elem => {

            val singlePosition = (elem \ "@position" text).toInt

            UniProtLocation(
              begin = singlePosition,
              end   = singlePosition
            )
          }
        }

      entry \ "feature" map {
        elem => (
          conversions.stringToFeatureType(elem \ "@type" text),
          (elem \ "@description").headOption map { _.text },
          parseLocation(elem)
        )
      }
    }
}

case class UniProtLocation(val begin: Int, val end: Int)
case class ZeroHalfOpenLocation(val begin: Int, val end: Int)
case object ZeroHalfOpenLocation {

  // I *think* UniProt intervals are 1-based and [begin,end]. Thus if we want 0-based [x,y[ then the start is -1 and the end is -1 + 1 = 0
  def fromUniProtLocation(uniprotLocation: UniProtLocation): ZeroHalfOpenLocation =
    ZeroHalfOpenLocation(
      begin = uniprotLocation.begin - 1,
      end   = uniprotLocation.end
    )
}
case object Entry {

  implicit def asXML: Entry => Elem = _.entry
}
