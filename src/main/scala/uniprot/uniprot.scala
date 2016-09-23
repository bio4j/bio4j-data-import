package com.bio4j.data.uniprot

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.xml._
import scala.compat.java8.OptionConverters._

// from keywords-all.tsv
case class KeywordRow(val id: String, val description: String, val category: String)

case class Process[V,E](val graph: UniProtGraph[V,E]) {

  type G = UniProtGraph[V,E]

  val entryProteins =
    GraphProcess.generically[V,E] (
      graph,
      (entry: Entry, g: G) =>
        (
          graph,
          Seq (
            g.protein.addVertex
            .set( g.protein.id,           entry.canonicalID     )
            .set( g.protein.accession,    entry.accession       )
            .set( g.protein.fullName,     entry.proteinFullName )
            .set( g.protein.existence,    entry.existence       )
            .set( g.protein.dataset,      entry.dataset         )
            .set( g.protein.sequence,     entry.sequence        )
          )
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
