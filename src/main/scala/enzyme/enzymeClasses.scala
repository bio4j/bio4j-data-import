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

  def isClass =
    classID == ID

  def isSubClass =
    subClassID == ID

  def isSubSubClass =
    subSubClassID == ID

  def IDFragments: (String,String,String,String) = {

    val fragments = ID.split('.').take(4)

    (fragments(0), fragments(1), fragments(2), fragments(3))
  }

  def classID = {
    val frmgts = IDFragments
    s"${frmgts._1}.-.-.-"
  }

  def subClassID = {
    val frmgts = IDFragments
    s"${frmgts._1}.${frmgts._2}.-.-"
  }

  def subSubClassID = {
    val frmgts = IDFragments
    s"${frmgts._1}.${frmgts._2}.${frmgts._3}.-"
  }
}
