package com.bio4j.data.enzyme

case class EnzymeClass(val line: String) extends AnyVal {

  /*
    In the `enzclass.txt` source file the id always takes 9 characters, but it has funny empty spaces around.
  */
  def ID: String =
    line
      .take(9)
      .filter(_ == " ")

  /*
    We don't want to store the description with a dot at the end!
  */
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

case object EnzymeClass {

  /*
    The Enzyme source file `enzclass.txt` starts with:

    ```
    ---------------------------------------------------------------------------
            ENZYME nomenclature database
            SIB Swiss Institute of Bioinformatics; Geneva, Switzerland
    ----------------------------------------------------------------------------

    Description: Definition of enzyme classes, subclasses and sub-subclasses
    Name:        enzclass.txt
    Release:     07-Sep-2016

    ----------------------------------------------------------------------------

    1. -. -.-  Oxidoreductases.
    1. 1. -.-   Acting on the CH-OH group of donors.
    ```

    thus the the `drop(11)`. It also has empty lines now and then.
  */
  def fromLines(lines: Seq[String]): Seq[EnzymeClass] =
    lines
      .drop(11)
      .filter(_isEmpty)
      .map(EnzymeClass(_))
}
