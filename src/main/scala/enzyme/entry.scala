package com.bio4j.data.enzyme

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

  def subSubClassID =
    s"${ID.reverse.dropWhile(_ != '.').reverse}-"
}

case object Entry {
  /*
    See ftp://ftp.expasy.org/databases/enzyme/enzuser.txt
  */
  def isValid(entry: Entry): Boolean =
    !( entry.description.startsWith("Deleted entry") || entry.description.startsWith("Transferred entry") )
}
