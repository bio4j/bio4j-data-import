package com.bio4j.release.generic.enzyme

import com.bio4j.release.generic._
import com.bio4j.model._
import com.bio4j.angulillos._
import com.bio4j.data.enzyme._
import scala.compat.java8.OptionConverters._

case class ImportEnzyme[V,E](val graph: ENZYMEGraph[V,E]) {

  // just an alias for convenience
  type G = ENZYMEGraph[V,E]
  def g: G = graph

  /* Enzyme classes need to be imported first */
  def enzymeClass(enzClass: EnzymeClass): G#EnzymeClass =
    g.enzymeClass.addVertex
      .set(g.enzymeClass.id, enzClass.ID)

  /* Subclasses require classes to be already imported */
  def enzymeSubClass(enzSubClass: EnzymeSubClass): G#EnzymeSubClass = {

    val subClass =
      g.enzymeSubClass.addVertex
        .set(g.enzymeSubClass.id, enzSubClass.ID)

    // TODO add it to data.enzyme
    // val toClass =
    //   findClass(g, enzClass.classID) map { clss =>
    //     ( subClass, g.subClasses.addEdge(clss, subClass) )
    //   }

    subClass
  }

  /* Subsubclasses need all subclasses already imported */
  def enzymeSubSubClass(enzSubSubClass: EnzymeSubSubClass): G#EnzymeSubSubClass = {

    val subSubClass =
      g.enzymeSubSubClass.addVertex
        .set(g.enzymeSubSubClass.id, enzSubSubClass.ID)

    // TODO add it to data.enzyme
    // val toSubClass =
    //   findSubClass(g, enzClass.subClassID) map { subClass =>
    //     ( subSubClass, g.subSubClasses.addEdge(subClass, subSubClass) )
    //   }

    subSubClass
  }

  /* Enzymes need all subsubclasses already imported */
  def enzyme(entry: AnyEntry): (G#Enzyme, Option[G#Enzymes]) = {

    val enzyme =
      g.enzyme.addVertex
        .set(g.enzyme.id, entry.ID)
        .set(g.enzyme.name, entry.description)
        .set(g.enzyme.alternateNames, entry.alternativeNames.toArray)
        .set(g.enzyme.cofactors, entry.cofactors.toArray)
        .set(g.enzyme.comments, entry.comments.mkString(" "))
        .set(g.enzyme.catalyticActivity, entry.catalyticActivity)

    val subsubclass =
      findSubSubClass( g, entry.subSubClassID )

    val memberOf =
      subsubclass.map { g.enzymes.addEdge(_, enzyme) }

    (enzyme, memberOf)
  }

  private def findClass(g: G, id: String): Option[G#EnzymeClass] =
    g.enzymeClass.id.index.find(id).asScala

  private def findSubClass(g: G, id: String): Option[G#EnzymeSubClass] =
    g.enzymeSubClass.id.index.find(id).asScala

  private def findSubSubClass(g: G, id: String): Option[G#EnzymeSubSubClass] =
    g.enzymeSubSubClass.id.index.find(id).asScala
}
