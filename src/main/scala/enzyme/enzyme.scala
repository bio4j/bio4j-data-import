package com.bio4j.data.enzyme

import com.bio4j.data._
import com.bio4j.model._
import com.bio4j.angulillos._
import scala.compat.java8.OptionConverters._

case class Process[V,E](val graph: ENZYMEGraph[V,E]) {

  type G = ENZYMEGraph[V,E]

  import EnzymeClass._

  // first import all classes
  val enzymeClasses =
    GraphProcess.generically[V,E] (
      graph,
      (enzClass: EnzymeClass, g: G) =>
        (
          graph,
          if(enzClass.isClass)
            Some(
              g.enzymeClass.addVertex
                .set(g.enzymeClass.id, enzClass.ID)
            )
          else
            None
        )
    )

  // needs all classes imported
  val enzymeSubClasses =
    GraphProcess.generically[V,E] (
      graph,
      (enzClass: EnzymeClass, g: G) =>
        (
          graph,

          if(enzClass.isSubClass) {

            val subClass =
              g.enzymeSubClass.addVertex
                .set(g.enzymeSubClass.id, enzClass.ID)

            findClass(g, enzClass.classID) map { clss =>
              ( subClass, g.subClasses.addEdge(clss, subClass) )
            }
          }
          else
            None
        )
    )

  // needs all subclasses imported
  val enzymeSubSubClasses =
    GraphProcess.generically[V,E] (
      graph,
      (enzClass: EnzymeClass, g: G) =>
        (
          graph,

          if(enzClass.isSubSubClass) {

            val subSubClass =
              g.enzymeSubSubClass.addVertex
                .set(g.enzymeSubSubClass.id, enzClass.ID)

            findSubClass(g, enzClass.subClassID) map { subClass =>
              ( subSubClass, g.subSubClasses.addEdge(subClass, subSubClass) )
            }
          }
          else
            None
        )
    )

  // needs subsubclasses first
  val enzymes =
    GraphProcess.generically[V,E] (
      graph,
      (entry: Entry, g: G) =>
        (
          graph,
          {
            val enzyme = g.enzyme.addVertex
              .set(g.enzyme.id, entry.ID)
              .set(g.enzyme.name, entry.description)
              .set(g.enzyme.alternateNames, entry.alternativeNames.toArray)
              .set(g.enzyme.cofactors, entry.cofactors.toArray)
              .set(g.enzyme.comments, entry.comments)
              .set(g.enzyme.catalyticActivity, entry.catalyticActivity)

            val subsubclass =
              findSubSubClass( g, entry.subSubClassID )

            val memberOf = subsubclass.foreach { ssc =>
              g.enzymes.addEdge(ssc, enzyme)
            }

            (enzyme, memberOf)
          }
        )
    )

    private def findClass(g: G, id: String): Option[G#EnzymeClass] =
      g.enzymeClass.id.index.find(id).asScala

    private def findSubClass(g: G, id: String): Option[G#EnzymeSubClass] =
      g.enzymeSubClass.id.index.find(id).asScala

    private def findSubSubClass(g: G, id: String): Option[G#EnzymeSubSubClass] =
      g.enzymeSubSubClass.id.index.find(id).asScala
}
