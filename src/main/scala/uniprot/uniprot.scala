package com.bio4j.release.generic

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.compat.java8.OptionConverters._
import bio4j.data.uniprot._

// from keywords-all.tsv
case class KeywordRow(val id: String, val description: String, val category: String)

case class ImportUniProt[V,E](val graph: UniProtGraph[V,E]) {

  type G = UniProtGraph[V,E]
  def g: G = graph

  /* This class represents pairs of entries and the corresponding canonical protein */
  case class EntryProtein(val entry: AnyEntry, val protein: G#Protein)

  /*
    This method imports the entry canonical protein *and* all isoforms, adding *isoforms* edges between them. All properties of the canonical protein are set, while for isoforms *sequences* are missing: they are imported from a separate fasta file.

    The return value corresponds to `(e, entryProtein, isoforms)`.
  */
  def allProteins(e: AnyEntry): (EntryProtein, Seq[G#Protein]) = {

    val entryProteinAccession =
      e.accessionNumbers.primary

    val isoformComments =
      e.comments collect { case i: Isoform => i }

    val entryProteinID =
      isoformComments.filter(_.isEntry).headOption.fold(entryProteinAccession)(_.id)

    val isoforms =
      isoformComments filterNot { _.isEntry }

    // either there's a recommended name or a submitted name
    val entryProteinFullName =
      e.description.recommendedName
        .fold(e.description.submittedNames.head.full)(_.full)

    val dataset =
      conversions.statusToDatasets( e.identification.status )

    val existence =
      conversions.proteinExistenceToExistenceEvidence( e.proteinExistence )

    /* All protein properties are set at this point: */
    val entryProtein =
      g.protein.addVertex
        .set(g.protein.id,              entryProteinID)
        .set(g.protein.accession,       entryProteinAccession)
        .set(g.protein.fullName,        entryProteinFullName)
        .set(g.protein.dataset,         dataset)
        .set(g.protein.sequence,        e.sequence.value)
        .set(g.protein.sequenceLength,  e.sequenceHeader.length: Integer )
        .set(g.protein.mass,            e.sequenceHeader.molecularWeight: Integer)

    // only newly imported isoform vertices are here
    val isoformVertices =
      isoforms collect {
        scala.Function.unlift { isoform =>

          g.protein.id.index.find(isoform.id).asScala
            .fold[Option[G#Protein]]({
              // need to add the new isoform
              val isoformV =
                g.protein.addVertex
                  .set(g.protein.id, isoform.id)
                  .set(g.protein.fullName, s"${e.description.recommendedName.fold(e.description.submittedNames.head.full)(_.full)} ${isoform.name}")

              val edge = g.isoforms.addEdge(entryProtein, isoformV)
              Some(isoformV)
            })(
              // already there; add an edge from the current entry protein
              isoformV => { g.isoforms.addEdge(entryProtein, isoformV); None }
            )
        }
      }

    (EntryProtein(e, entryProtein), isoformVertices)
  }

  def geneNames(entryProtein: EntryProtein): (EntryProtein, Seq[G#GeneName]) = {

    val geneNames: Seq[String] =
      validGeneNames(entryProtein.entry.geneNames)

    val newGeneNames = geneNames collect {
      scala.Function.unlift { name =>

        val present =
          g.geneName.name.index.find(name)
            .asScala

        present.fold[Option[G#GeneName]]({
            val newGeneName =
              g.geneName.addVertex
                .set(g.geneName.name, name)

            val edge = g.geneProducts.addEdge(newGeneName, entryProtein.protein)
            Some(newGeneName)
          }
        )(
          // gene name vertex present, only add edge
          geneName => {
            g.geneProducts.addEdge(geneName, entryProtein.protein)
            None
          }
        )
      }
    }

    (entryProtein, newGeneNames)
  }

  def keywords(entryProtein: EntryProtein): (EntryProtein, Seq[G#Keywords]) = {

    val keywords =
      entryProtein.entry.keywords

    val keywordEdges =
      keywords collect {
        scala.Function.unlift { kw =>
          g.keyword.id.index.find(kw.id).asScala.map { g.keywords.addEdge(entryProtein.protein, _) }
        }
      }

    (entryProtein, keywordEdges)
  }

  def comments(entryProtein: EntryProtein): (EntryProtein, Seq[G#Comment]) = {

    val entryComments: Seq[Comment] =
      entryProtein.entry.comments filterNot { x => x.isInstanceOf[Isoform] }

    val commentVertices =
      entryComments map { cc =>
        val comment = g.comment.addVertex
          .set(g.comment.topic, conversions.commentTopic(cc))
          .set(g.comment.text,  cc.asInstanceOf[{ val text: String }].text) // TODO needs bio4j/data.uniprot#19 or something similar

        g.comments.addEdge(entryProtein.protein, comment)
        comment
      }

    (entryProtein, commentVertices)
  }

  def features(entryProtein: EntryProtein): (EntryProtein, Seq[G#Annotation]) = {

    val entryFeatures =
      entryProtein.entry.features

    val annotationVertices =
      entryFeatures map { ft =>

        val annotationV =
          g.annotation.addVertex
            .set(g.annotation.featureType, conversions.featureKeyToFeatureType(ft.key))
            .set(g.annotation.description, ft.description)

        val annotationE =
          g.annotations.addEdge(entryProtein.protein, annotationV)
            .set(g.annotations.begin, conversions.featureFromAsInt(ft.from): Integer)
            .set(g.annotations.end, conversions.featureToAsInt(ft.to): Integer)

        annotationV
      }

    (entryProtein, annotationVertices)
  }

  def isoformSequencesFrom(fasta: IsoformFasta): Option[G#Protein] =
    g.protein.id.index.find(fasta.proteinID).asScala.map { isoform =>
      isoform
        .set(g.protein.sequence, fasta.sequence)
        .set(g.protein.sequenceLength, fasta.sequence.length: java.lang.Integer)
    }

  def keywordTypes(row: KeywordRow): G#Keyword = {

    val kwType =
      g.keyword.addVertex
        .set(g.keyword.id, row.id)
        .set(g.keyword.definition, row.description)

    conversions.stringToKeywordCategory(row.category).foreach { kwType.set(g.keyword.category, _) }

    kwType
  }

  private def validGeneNames(gns: Seq[GeneName]): Seq[String] =
    gns collect {
      scala.Function.unlift { gn =>
        gn.name.fold(gn.ORFNames.headOption)(n => Some(n.official))
      }
    }
}
