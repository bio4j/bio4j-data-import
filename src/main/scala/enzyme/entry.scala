package com.bio4j.data.enzyme

import scala.compat.java8.OptionConverters._

case class Entry(val lines: Seq[String]) extends AnyVal {

  def ID: String =
    lines.
      filter(_.startsWith("ID"))
      .map(_.trim)
      .head

  def description: String =
    lines
      .filter(_.startsWith("DE"))
      .map(_.trim)
      .mkString(" ")

  def alternativeNames: Seq[String] =
    lines
      .filter(_.startsWith("AN"))
      .map(_.trim.stripSuffix("."))

  def cofactors: Seq[String] =
    lines.
      filter(_.startsWith("CF"))
      .map(_.trim)
      .mkString("")
      .split(';')
      .map(_.trim.stripSuffix("."))

  def catalyticActivity: String =
    lines
      .filter(_.startsWith("CA"))
      .map(_.trim)
      .mkString(" ")

  def comments: String =
    lines
      .filter(_.startsWith("CC"))
      .map(_.trim)
      .mkString(" ")
}

case object Entry {
  /*
    See ftp://ftp.expasy.org/databases/enzyme/enzuser.txt
  */
  def isValid(entry: Entry): Boolean =
    !( entry.description.startsWith("Deleted entry") || entry.description.startsWith("Transferred entry") )
}

case object EnzymeClasses {

  def idFragments(id: String): (String,String,String,String) = {

    val fragments = id.split('.').take(4)
    
    (fragments(0), fragments(1), fragments(2), fragments(3))
  }

  def isEnzyme(id: String) =
    idFragments(id) match {
      case (_,_,_,b) if(b != "-") => true
      case _                      => false
    }

  def isSubSubClass(id: String) =
    idFragments(id) match {
      case (_,_,b,"-") if(b != "-") => true
      case _                        => false
    }

  def isSubClass(id: String) =
    idFragments(id) match {
      case (_,b,"-",_) if(b != "-") => true
      case _                        => false
    }

  def isClass(id: String) =
    idFragments(id) match {
      case (_,"-",_,_)  => true
      case _            => false
    }
}
