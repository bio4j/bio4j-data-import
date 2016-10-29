package com.bio4j.data.uniprot

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.compat.java8.OptionConverters._
import bio4j.data.uniprot.flat.{ Entry => FEntry }
import bio4j.data.uniprot._

// from keywords-all.tsv
case class KeywordRow(val id: String, val description: String, val category: String)

case class ImportUniProt[V,E](val graph: UniProtGraph[V,E]) {

  type G = UniProtGraph[V,E]

  def dataFrom(e: FEntry, g: G): G = {

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

    /* All properties are set at this point */
    val entryProtein =
      g.protein.addVertex
        .set(g.protein.id,              entryProteinID)
        .set(g.protein.accession,       entryProteinAccession)
        .set(g.protein.fullName,        entryProteinFullName)
        .set(g.protein.dataset,         dataset)
        .set(g.protein.sequence,        e.sequence.value)
        .set(g.protein.sequenceLength,  e.sequenceHeader.length: Integer )
        .set(g.protein.mass,            e.sequenceHeader.molecularWeight: Integer)

    /* Gene names */
    val geneNames = (e.geneNames map { gn => gn.name.fold(gn.ORFNames.headOption)(n => Some(n.official)) }).flatten

    geneNames.foreach { n =>

      val gn =
        g.geneName.name.index.find(n).asScala getOrElse
        g.geneName.addVertex
          .set(g.geneName.name, n)

      // add edges
      g.geneProducts.addEdge(gn, entryProtein)
        // .setProperty(g.geneProducts, ???) // gene locations?
    }

    /* Protein keywords. This needs the keyword types beforehand */
    val keywords = e.keywords
    keywords.foreach { kw =>
      g.keyword.id.index.find(kw.id).asScala.foreach { kwt =>
        g.keywords.addEdge(entryProtein, kwt)
      }
    }

    /* Other (non-isoform) comments */
    val entryComments: Seq[Comment] = ??? // comments filterNot { x => x.isInstanceOf[Isoform] } // TODO enable isInstanceOf here
    entryComments
      .map( cc =>
        g.comment.addVertex
          .set(g.comment.topic, conversions.commentTopic(cc))
          // .set(g.comment.text,  cc.text) needs bio4j/data.uniprot#19
      )
      .foreach { cv =>
        g.comments.addEdge(entryProtein, cv)
      }

    g
  }

}


case class Process[V,E](val graph: UniProtGraph[V,E]) {

  type G = UniProtGraph[V,E]

  val entryProteins =
    GraphProcess.generically[V,E] (
      graph,
      (entry: FEntry, g: G) =>
        (
          graph,
          g.protein.addVertex
          .set(g.protein.id, entry.accessionNumbers.primary) // TODO get from canonical isoform etc
          .set(g.protein.accession, entry.accessionNumbers.primary)
          .set(g.protein.fullName,  entry.description.recommendedName.map(_.full) getOrElse entry.description.submittedNames.head.full )
          .set(g.protein.existence, conversions.proteinExistenceToExistenceEvidence(entry.proteinExistence))
          .set(g.protein.dataset, conversions.statusToDatasets(entry.identification.status))
          .set(g.protein.sequence, entry.sequence.value)
          .set(g.protein.sequenceLength, entry.sequenceHeader.length: Integer)
        )
    )


  /*
    Import gene names vertices, and the geneProducts edge from gene names to entry proteins. Proteins should be already imported.
  */
  val entryGeneNames =
    GraphProcess.generically[V,E](
      graph,
      (entry: Entry, g: G) => {

        // if there's no gene name, empty, otherwise try to find if it's already imported, if not add it
        val geneNames =
          entry.geneName
            .fold( Seq[G#GeneName]() ) {
              name =>
              g.geneName.name.index.find(name).asScala
              .fold(
                Seq(
                  g.geneName.addVertex
                  .set(g.geneName.name, name)
                )
              ){ Seq(_) }
            }

        /* Add geneProducts edges to the entry protein. Proteins should be imported before. */
        g.protein.accession.index.find( entry.accession ).asScala.foreach {
          protein => geneNames foreach {
            geneName =>
              g.geneProducts.addEdge(geneName, protein)
                .set(g.geneProducts.location, entry.geneLocation)
          }
        }

        (graph, geneNames)
      }
    )


  /*
    Import entry-level comment annotations, and the comments edges from entry proteins to comments. Entry proteins should be already imported.
  */
  val comments =
    GraphProcess.generically[V,E](
      graph,
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
          graph.protein.accession.index.find( entry.accession ).asScala.foreach {
            protein => comments.foreach {
              comment => g.comments.addEdge( protein, comment )
            }
          }

        (graph, comments)
      }
    )

  /*
    Import all the keyword types. This does not need anything beforehand.
  */
  val keywordTypes =
    GraphProcess.generically[V,E](
      graph,
      (row: KeywordRow, g: G) =>
        (
          graph,
          Seq(
            g.keyword.addVertex
              .set(g.keyword.id, row.id)
              .set(g.keyword.definition, row.description)
              .set(g.keyword.category, conversions.stringToKeywordCategory(row.category))
          )
        )
    )

  val keywords =
    GraphProcess.generically[V,E](
      graph,
      (entry: Entry, g: UniProtGraph[V,E]) => {

        val keywords = entry.keywordIDs.map(g.keyword.id.index.find(_).asScala).flatten

        g.protein.accession.index.find(entry.accession).asScala foreach {
          protein => keywords foreach {
            keyword => g.keywords.addEdge(protein, keyword)
          }
        }

        (graph, keywords)
      }
    )

  /*
    Import sequence annotations (features), including the annotations edge from entry proteins to their annotations. This needs the entry proteins.
  */
  val annotations =
    GraphProcess.generically[V,E](
      graph,
      (entry: Entry, g: UniProtGraph[V,E]) => {

        val proteinOpt =
          graph.protein.accession.index.find( entry.accession ).asScala

        val importedAnnotations = entry.features map {
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

        (graph, importedAnnotations)
      }
    )

  /*
    Import the isoforms, and add isoforms edges from the entry protein to the isoforms described there. Needs the entry proteins imported before.
  */
  val isoforms =
    GraphProcess.generically[V,E](
      graph,
      (entry: Entry, g: UniProtGraph[V,E]) => {

        val maybeEntryProtein = g.protein.accession.index.find(entry.accession).asScala

        val importedIsoforms = entry.isoforms map {
          case (id, name) => {

            g.protein.id.index.find(id).asScala.fold(
              { // need to add the new isoform
                val isoform = g.protein.addVertex
                  .set(g.protein.id, id)
                  .set(g.protein.fullName, name)

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

        (graph, importedIsoforms)
      }
    )

  val isoformSequences =
    GraphProcess.generically[V,E](
      graph,
      (fasta: IsoformFasta, g: G) => {

        val isoformOpt = g.protein.id.index.find(fasta.proteinID).asScala

        isoformOpt.foreach {
          isoform =>
          val seq = fasta.sequence

          isoform
          .set(g.protein.sequence, seq)
          .set(g.protein.sequenceLength, seq.length: java.lang.Integer)
        }

        (graph, isoformOpt)
      }
    )
}
