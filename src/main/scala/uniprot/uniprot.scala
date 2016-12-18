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





















  /*
    ## UniProt entry data

    Here we import all data that is derived from one UniProt entry. This method assumes that the following has been already imported:

    - keyword types
    - ...
  */
  def dataFrom(e: AnyEntry, g: G): G = {

    val entryProteinAccession =
      e.accessionNumbers.primary

    // we need comments first, as isoforms are always there
    val comments =
      e.comments

    val isoforms =
      comments collect { case i: Isoform => i }

    val entryProteinID =
      isoforms.filter(_.isEntry).headOption.fold(entryProteinAccession)(_.id)

    // either there's a recommended name or a submitted name
    val entryProteinFullName =
      e.description.recommendedName
        .fold(e.description.submittedNames.head.full)(_.full)

    val dataset =
      conversions.statusToDatasets( e.identification.status )

    val existence =
      conversions.proteinExistenceToExistenceEvidence( e.proteinExistence )

    /*
      All protein properties are set at this point:
    */
    val entryProtein =
      g.protein.addVertex
        .set(g.protein.id,              entryProteinID)
        .set(g.protein.accession,       entryProteinAccession)
        .set(g.protein.fullName,        entryProteinFullName)
        .set(g.protein.dataset,         dataset)
        .set(g.protein.sequence,        e.sequence.value)
        .set(g.protein.sequenceLength,  e.sequenceHeader.length: Integer )
        .set(g.protein.mass,            e.sequenceHeader.molecularWeight: Integer)

    /*

      ### Gene names and their edges

      Gene names are ...
    */
    val geneNames =
      e.geneNames
        .map( gn =>
          gn.name.fold(gn.ORFNames.headOption)(n => Some(n.official))
        )
        .flatten

    geneNames.foreach { n =>

      val gn =
        g.geneName.name.index.find(n)
          .asScala
          .getOrElse( g.geneName.addVertex )
          .set(g.geneName.name, n)

      // add edges
      g.geneProducts.addEdge(gn, entryProtein)
        // .setProperty(g.geneProducts, ???) // gene locations?
    }

    /*
      ### Protein keywords

      This needs the keyword types beforehand.
    */
    val keywords =
      e.keywords

    keywords.foreach { kw =>
      g.keyword.id.index.find(kw.id).asScala.foreach { kwt =>
        g.keywords.addEdge(entryProtein, kwt)
      }
    }

    /*
      ### Protein comments

    */
    val entryComments: Seq[Comment] = comments filterNot { x => x.isInstanceOf[Isoform] } // TODO enable isInstanceOf here
    entryComments
      .map( cc =>
        g.comment.addVertex
          .set(g.comment.topic, conversions.commentTopic(cc))
          // .set(g.comment.text,  cc.text) // TODO needs bio4j/data.uniprot#19 or something similar
      )
      .foreach { cv =>
        g.comments.addEdge(entryProtein, cv)
      }
    /*
      ### Protein annotations

    */
      val proteinOpt =
        graph.protein.accession.index.find( e.accessionNumbers.primary ).asScala

      proteinOpt.foreach { protein =>

        e.features foreach { feature =>

          val annotation =
            g.annotation.addVertex
              .set(g.annotation.featureType, conversions.featureKeyToFeatureType(feature.key))
              .set(g.annotation.description, feature.description)

          g.annotations.addEdge(protein, annotation)
            .set(g.annotations.begin, conversions.featureFromAsInt(feature.from): Integer)
            .set(g.annotations.end, conversions.featureToAsInt(feature.to): Integer)
        }
      }

    g
  }

  /*
    ## Isoforms

    Import the isoforms, and add isoforms edges from the entry protein to the isoforms described there. Needs the entry proteins imported before.
  */
  def isoformsFrom(e: AnyEntry, g: G): G = {

    val isoforms =
      (e.comments collect { case i: Isoform => i })
      .filterNot(_.isEntry)

    val maybeEntryProtein = g.protein.accession.index.find(e.accessionNumbers.primary).asScala

    isoforms foreach { isoform =>

      g.protein.id.index.find(isoform.id).asScala.fold(
        { // need to add the new isoform
          val isoformV = g.protein.addVertex
            .set(g.protein.id, isoform.id)
            .set(g.protein.fullName, s"${e.description.recommendedName
              .fold(e.description.submittedNames.head.full)(_.full)} ${isoform.name}")

          maybeEntryProtein.foreach { protein => g.isoforms.addEdge(protein, isoformV) }
        }
      ){ // already there; add an edge from the current entry protein
        isoform =>
          maybeEntryProtein.foreach { protein => g.isoforms.addEdge(protein, isoform) }
      }
    }

    g
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
