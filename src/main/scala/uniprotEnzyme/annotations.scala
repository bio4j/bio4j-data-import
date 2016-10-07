package com.bio4j.data.uniprotEnzyme

import scala.xml._
import com.bio4j.data.uniprot.Entry
import com.bio4j.data.enzyme._

case class EntryEnzymeAnnotations(val entry: Elem) extends AnyVal {

  def IDs: Seq[EnzymeID] =
    this.dbReferenceIDs("EC").map( EnzymeID(_) )
}

case object EntryEnzymeAnnotations {

  implicit def entryEnzymeAnnotations(entry: Entry): EntryEnzymeAnnotations =
    EntryEnzymeAnnotations(entry.entry)

  implicit def asEntry(entryEnzymeAnnotations: EntryEnzymeAnnotations): Entry =
    Entry(entryEnzymeAnnotations.entry)
}
