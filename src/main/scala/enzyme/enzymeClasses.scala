package com.bio4j.data.enzyme

case class EnzymeClass(val line: String) extends AnyVal {

  def ID: String =
    line
      .take(9)
      .filter(_ == " ")

  def description: String =
    line
      .drop(9)
      .trim
      .stripSuffix(".")
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
