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
    (entry \ "accession" head).text

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

  def geneLocation: UniProtGraph.GeneLocations =
    ( entry \ "geneLocation" \ "@type" ).headOption.fold(UniProtGraph.GeneLocations.chromosome) {
      elem => conversions.stringToGeneLocation(elem.text)
    }

  def comments: Seq[(UniProtGraph.CommentTopics, String)] =
    entry \ "comment" map {
      elem => ( conversions.stringToCommentTopic( elem \ "@type" text ), elem \ "text" text )
    }

  def keywordIDs: Seq[String] =
    entry \ "keyword" map { _ \ "@id" text }

  /*
    The output here is (type, description, positionInfo).
  */
  def features: Seq[(UniProtGraph.FeatureTypes, Option[String], UniProtLocation)] =
    {

      def parseLocation(location: Node): UniProtLocation =
        (location \ "position").headOption.fold(
          {
            // both positions can be empty!
            val _begin = (location \ "begin" \ "@position" text) match {
              case ""   => 0
              case pos  => pos.toInt
            }

            val _end = (location \ "end" \ "@position" text) match {
              case ""   => 0
              case pos  => length
            }

            UniProtLocation(
              begin = _begin,
              end   = _end
            )
          }
        ){
          elem => {

            val singlePosition = (elem \ "@position" text).toInt

            UniProtLocation(
              begin = singlePosition,
              end   = singlePosition
            )
          }
        }

      (entry \ "feature").map {
        elem: Node => {
          (
            conversions.stringToFeatureType(elem \ "@type" text),
            (elem \ "@description").headOption map { _.text },
            parseLocation(elem \ "location" head)
          )
        }
      }
    }

  // returns (id, name)
  def isoforms: Seq[(String, String)] =
    (entry \ "comment" \ "isoform")
      .filter { elem => (elem \ "sequence" \ "@type" text) != "displayed" } // exclude the one corresponding to the entry
      .map { elem => (elem \ "id" text, s"${proteinFullName} isoform ${(elem \ "name" text)}") }

  def canonicalID: String = {

    val isoforms = (entry \ "comment" \ "isoform")

    if (isoforms.isEmpty)
      accession
    else
      isoforms
        .filter { elem => (elem \ "sequence" \ "@type" text) == "displayed" }
        .map { _ \ "id" text }
        .head
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

  /*
    This method is an ugly hack for getting an iterator of entries from UniProt xml.
  */
  def fromUniProtLines(lines: Iterator[String]): Iterator[Entry] = new Iterator[Entry] {

    private val rest: BufferedIterator[String] = lines.buffered
    private val currentEntryStringBuilder = new collection.mutable.StringBuilder

    private var _hasNextCalled: Boolean = false
    private var _hasNext      : Boolean = false

    private lazy val MyXML = new factory.XMLLoader[Elem] {

      private lazy val parserFactory = {
        val f0 = javax.xml.parsers.SAXParserFactory.newInstance()
        f0.setNamespaceAware(false)
        f0.setValidating(false)
        f0.setXIncludeAware(false)
        f0
      }

      private lazy val parser0 = parserFactory.newSAXParser()

      override def parser: SAXParser = parser0
    }

    /*
      note that internally hasNext drops everything it founds before a line starting with '<entry'.
    */
    def hasNext: Boolean = if(_hasNextCalled) _hasNext else {

      _hasNext = advanceUntilNextEntry
      _hasNextCalled = true
      _hasNext
    }

    def next(): Entry = {

      if(hasNext) {
        _hasNextCalled = false;
        takeEntry
      }
      else throw new NoSuchElementException
    }

    private def isEntryStart(line: String): Boolean = line startsWith "<entry"
    private def isEntryStop(line: String): Boolean  = line startsWith "</entry"

    private def advanceUntilNextEntry: Boolean = {

      if(rest.hasNext) {

        val nextLine = rest.head

        if(isEntryStart(nextLine))
          true
        else {
          rest.next
          advanceUntilNextEntry
        }
      }
      else
        false
    }

    @annotation.tailrec
    private def takeEntry_rec(acc: collection.mutable.StringBuilder): Entry =
      if( !isEntryStop(rest.head) )
        takeEntry_rec(acc ++= rest.next)
      else
        Entry( MyXML.loadString( (acc ++= rest.next).toString ) )

    private def takeEntry: Entry = {
      currentEntryStringBuilder.setLength(0) // clear the string builder
      takeEntry_rec(currentEntryStringBuilder)
    }
  }
}
