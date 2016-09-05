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

case class Keyword[V,E](val graph: UniProtGraph[V,E]) {

  val fromRow = AddVertex.generically[V,E](
    graph,
    graph.keyword,
    (row: KeywordRow, g: UniProtGraph[V,E]) =>
      Seq(
        g.keyword.addVertex
          .set(g.keyword.name, row.id)
          .set(g.keyword.definition, row.description)
          .set(g.keyword.category, stringToKeywordCategory(row.category))
      )
  )

  val stringToKeywordCategory: String => UniProtGraph.KeywordCategories =
    {
      case "Biological process"         => UniProtGraph.KeywordCategories.biologicalProcess
      case "Cellular component"         => UniProtGraph.KeywordCategories.cellularComponent
      case "Coding sequence diversity"  => UniProtGraph.KeywordCategories.codingSequenceDiversity
      case "Developmental stage"        => UniProtGraph.KeywordCategories.developmentalStage
      case "Disease"                    => UniProtGraph.KeywordCategories.disease
      case "Domain"                     => UniProtGraph.KeywordCategories.domain
      case "Ligand"                     => UniProtGraph.KeywordCategories.ligand
      case "Molecular function"         => UniProtGraph.KeywordCategories.molecularFunction
      case "null"                       => ??? // TODO do something with this
      case "PTM"                        => UniProtGraph.KeywordCategories.PTM
      case "Technical term"             => UniProtGraph.KeywordCategories.technicalTerm
    }
}

case class Feature[V,E](val graph: UniProtGraph[V,E]) {


  val stringToFeatureType: String => UniProtGraph.FeatureTypes =
    {
      case "active site"                          => UniProtGraph.FeatureTypes.activeSite
      case "binding site"                         => UniProtGraph.FeatureTypes.bindingSite
      case "calcium-binding region"               => UniProtGraph.FeatureTypes.calciumBindingRegion
      case "chain"                                => UniProtGraph.FeatureTypes.chain
      case "coiled-coil region"                   => UniProtGraph.FeatureTypes.coiledCoilRegion
      case "compositionally biased region"        => UniProtGraph.FeatureTypes.compositionallyBiasedRegion
      case "cross-link"                           => UniProtGraph.FeatureTypes.crosslink
      case "disulfide bond"                       => UniProtGraph.FeatureTypes.disulfideBond
      case "DNA-binding region"                   => UniProtGraph.FeatureTypes.DNABindingRegion
      case "domain"                               => UniProtGraph.FeatureTypes.domain
      case "glycosylation site"                   => UniProtGraph.FeatureTypes.glycosylationSite
      case "helix"                                => UniProtGraph.FeatureTypes.helix
      case "initiator methionine"                 => UniProtGraph.FeatureTypes.initiatorMethionine
      case "lipid moiety-binding region"          => UniProtGraph.FeatureTypes.lipidMoietyBindingRegion
      case "metal ion-binding site"               => UniProtGraph.FeatureTypes.metalIonBindingSite
      case "modified residue"                     => UniProtGraph.FeatureTypes.modifiedResidue
      case "mutagenesis site"                     => UniProtGraph.FeatureTypes.mutagenesisSite
      case "non-consecutive residues"             => UniProtGraph.FeatureTypes.nonConsecutiveResidues
      case "non-terminal residue"                 => UniProtGraph.FeatureTypes.nonTerminalResidue
      case "nucleotide phosphate-binding region"  => UniProtGraph.FeatureTypes.nucleotidePhosphateBindingRegion
      case "peptide"                              => UniProtGraph.FeatureTypes.peptide
      case "propeptide"                           => UniProtGraph.FeatureTypes.propeptide
      case "region of interest"                   => UniProtGraph.FeatureTypes.regionOfInterest
      case "repeat"                               => UniProtGraph.FeatureTypes.repeat
      case "non-standard amino acid"              => UniProtGraph.FeatureTypes.nonstandardAminoAcid
      case "sequence conflict"                    => UniProtGraph.FeatureTypes.sequenceConflict
      case "sequence variant"                     => UniProtGraph.FeatureTypes.sequenceVariant
      case "short sequence motif"                 => UniProtGraph.FeatureTypes.shortSequenceMotif
      case "signal peptide"                       => UniProtGraph.FeatureTypes.signalPeptide
      case "site"                                 => UniProtGraph.FeatureTypes.site
      case "splice variant"                       => UniProtGraph.FeatureTypes.spliceVariant
      case "strand"                               => UniProtGraph.FeatureTypes.strand
      case "topological domain"                   => UniProtGraph.FeatureTypes.topologicalDomain
      case "transit peptide"                      => UniProtGraph.FeatureTypes.transitPeptide
      case "transmembrane region"                 => UniProtGraph.FeatureTypes.transmembraneRegion
      case "turn"                                 => UniProtGraph.FeatureTypes.turn
      case "unsure residue"                       => UniProtGraph.FeatureTypes.unsureResidue
      case "zinc finger region"                   => UniProtGraph.FeatureTypes.zincFingerRegion
      case "intramembrane region"                 => UniProtGraph.FeatureTypes.intramembraneRegion
    }
}
