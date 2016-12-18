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

  /*
    This method imports the entry canonical protein *and* all isoforms, adding *isoforms* edges between them. All properties of the canonical protein are set, while for isoforms *sequences* are missing: they are imported from a separate fasta file.

    The return value corresponds to `(e, entryProtein, isoforms)`.
  */
  def allProteins(e: AnyEntry): (AnyEntry, G#Protein, Seq[G#Protein]) = {

    val entryProteinAccession =
      e.accessionNumbers.primary

    // we need comments first, as isoforms are always there
    val comments =
      e.comments

    val isoformComments =
      comments collect { case i: Isoform => i }

    val entryProteinID =
      isoformComments.filter(_.isEntry).headOption.fold(entryProteinAccession)(_.id)

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

    val isoforms =
      isoformComments filterNot { _.isEntry }

    // only newly imported isoform vertices are here
    val isoformVertices =
      isoforms collect {
        scala.Function.unlift { isoform =>

          val findIsoform =
            g.protein.id.index.find(isoform.id).asScala

          findIsoform.fold[Option[G#Protein]]({
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

    (e, entryProtein, isoformVertices)
  }

  private def validGeneNames(gns: Seq[GeneName]): Seq[String] =
    gns collect {
      scala.Function.unlift { gn =>
        gn.name.fold(gn.ORFNames.headOption)(n => Some(n.official))
      }
    }

  /* **IMPORTANT** `e` is assumed to be the entry corresponding to `entryProtein` */
  def geneNames(e: AnyEntry, entryProtein: G#Protein): (AnyEntry, Seq[G#GeneName]) = {

    val geneNames: Seq[String] =
      validGeneNames(e.geneNames)

    val newGeneNames = geneNames collect {
      scala.Function.unlift { name =>

        val present =
          g.geneName.name.index.find(name)
            .asScala

        present.fold[Option[G#GeneName]]({
            val newGeneName =
              g.geneName.addVertex
                .set(g.geneName.name, name)

            val edge = g.geneProducts.addEdge(newGeneName, entryProtein)
            Some(newGeneName)
          }
        )(
          // gene name vertex present, only add edge
          geneName => {
            g.geneProducts.addEdge(geneName, entryProtein)
            None
          }
        )
      }
    }

    (e, newGeneNames)
  }

  def keywords(e: AnyEntry, entryProtein: G#Protein): (AnyEntry, Seq[G#Keywords]) = {

    val keywords =
      e.keywords

    val keywordEdges =
      keywords collect {
        scala.Function.unlift { kw =>
          g.keyword.id.index.find(kw.id).asScala.map { g.keywords.addEdge(entryProtein, _) }
        }
      }

    (e, keywordEdges)
  }

  def comments(e: AnyEntry, entryProtein: G#Protein): (AnyEntry, Seq[G#Comment]) = {

    val entryComments: Seq[Comment] =
      e.comments filterNot { x => x.isInstanceOf[Isoform] }

    val commentVertices =
      entryComments map { cc =>
        val comment = g.comment.addVertex
          .set(g.comment.topic, conversions.commentTopic(cc))
          .set(g.comment.text,  cc.asInstanceOf[{ val text: String }].text) // TODO needs bio4j/data.uniprot#19 or something similar

        g.comments.addEdge(entryProtein, comment)
        comment
      }

    (e, commentVertices)
  }

  def features(e: AnyEntry, entryProtein: G#Protein): (AnyEntry, Seq[G#Annotation]) = {

    val entryFeatures =
      e.features

    val annotationVertices =
      entryFeatures map { ft =>

        val annotationV =
          g.annotation.addVertex
            .set(g.annotation.featureType, conversions.featureKeyToFeatureType(ft.key))
            .set(g.annotation.description, ft.description)

        val annotationE =
          g.annotations.addEdge(entryProtein, annotationV)
            .set(g.annotations.begin, conversions.featureFromAsInt(ft.from): Integer)
            .set(g.annotations.end, conversions.featureToAsInt(ft.to): Integer)

        annotationV
      }

    (e, annotationVertices)
  }

  /*
    ## Isoform sequences
  */
  def isoformSequencesFrom(fasta: IsoformFasta, g: G): G = {

    g.protein.id.index.find(fasta.proteinID).asScala.foreach { isoform =>

      val seq =
        fasta.sequence

      val isoformWithSeq =
        isoform
          .set(g.protein.sequence, seq)
          .set(g.protein.sequenceLength, seq.length: java.lang.Integer)
    }

    g
  }

  def keywordTypesFrom(row: KeywordRow, g: G): G = {

    val kwType =
      g.keyword.addVertex
        .set(g.keyword.id, row.id)
        .set(g.keyword.definition, row.description)

    conversions.stringToKeywordCategory(row.category).foreach { kwType.set(g.keyword.category, _) }

    g
  }
}
