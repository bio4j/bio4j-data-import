// package com.bio4j.data.uniprotTaxonomy
//
// import scala.xml._
// import com.bio4j.data.uniprot.Entry
//
// case class EntryTaxonomyAnnotations(val entry: Elem) extends AnyVal {
//
//   def hosts: Seq[String] =
//     (entry \ "organismHost" \ "dbReference") map { _ \ "@id" text }
//
//   def organism: String =
//     (entry \ "organism" \ "dbReference" \ "@id").head.text
// }
//
// case object EntryTaxonomyAnnotations {
//
//   implicit def entryTaxonomyAnnotations(entry: Entry): EntryTaxonomyAnnotations =
//     EntryTaxonomyAnnotations(entry.entry)
//
//   implicit def asEntry(entryTaxonomyAnnotations: EntryTaxonomyAnnotations): Entry =
//     Entry(entryTaxonomyAnnotations.entry)
// }
