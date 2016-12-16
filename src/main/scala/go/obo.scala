// package com.bio4j.data.go
//
// import com.bio4j.data._
// import com.bio4j.model._
// import com.bio4j.angulillos._
// import scala.xml._
// import scala.compat.java8.OptionConverters._
//
// case class Obo(xml: Elem) extends AnyVal {
//
//   def terms: Seq[XmlTerm] =
//     (xml \ "term").filter( term => (term \ "isObsolete").isEmpty ) map XmlTerm
//
//   def isA: Seq[Rel] =
//     terms flatMap { _.isA }
//
//   def partOf: Seq[Rel] =
//     terms flatMap { _.partOf }
//
//   def regulates: Seq[Rel] =
//     terms flatMap { _.regulates }
//
//   def negativelyRegulates: Seq[Rel] =
//     terms flatMap { _.negativelyRegulates }
//
//   def positivelyRegulates: Seq[Rel] =
//     terms flatMap { _.positivelyRegulates }
// }
//
// case class XmlTerm(val xml: Node) extends AnyVal {
//
//   def ID: String =
//     (xml \ "id").head.text
//
//   def name: String =
//     (xml \ "name").head.text
//
//   def comment: String =
//     (xml \ "comment").head.text
//
//   def namespace: String =
//     (xml \ "namespace").head.text
//
//   def definition: String =
//     (xml \ "def" \ "defstr").head.text
//
//   // for some reason isA has a different structure
//   def isA: Seq[Rel] =
//     (xml \ "is_a") map { elem => Rel(ID, elem.text) }
//
//   def partOf: Seq[Rel] =
//     relationships("part_of")
//
//   def regulates: Seq[Rel] =
//     relationships("regulates")
//
//   def positivelyRegulates: Seq[Rel] =
//     relationships("positively_regulates")
//
//   def negativelyRegulates: Seq[Rel] =
//     relationships("negatively_regulates")
//
//   private def relFromRelationship(relationship: Node): Rel =
//     Rel(ID, (relationship \ "to").head.text)
//
//   private def relationshipIs(name: String)(relationship: Node): Boolean =
//     (relationship \ "type").head.text == name
//
//   private def relationships(name: String) =
//     (xml \ "relationship")
//       .filter( relationshipIs(name) )
//       .map( relFromRelationship )
// }
//
// case class Rel(sourceID: String, targetID: String)
