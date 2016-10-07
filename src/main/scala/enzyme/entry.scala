package com.bio4j.data.enzyme

case class Entry(val lines: Seq[String]) extends AnyVal {

  def ID: String =
    lines.
      filter(_.startsWith("ID"))
      .head
      .stripPrefix("ID")
      .trim

  def description: String =
    lines
      .filter(_.startsWith("DE"))
      .map(_.stripPrefix("DE").trim.stripSuffix("."))
      .mkString(" ")

  def alternativeNames: Seq[String] =
    lines
      .filter(_.startsWith("AN"))
      .map(_.stripPrefix("AN").trim)
      .mkString(" ")
      .split('.').toList

  def cofactors: Seq[String] =
    lines.
      filter(_.startsWith("CF"))
      .map(_.stripPrefix("CF").trim)
      .mkString("")
      .split(';').toList
      .map(_.trim.stripSuffix("."))

  def catalyticActivity: String =
    lines
      .filter(_.startsWith("CA"))
      .map(_.stripPrefix("CA").trim)
      .mkString(" ")

  def comments: String =
    lines
      .filter(_.startsWith("CC"))
      .map(_.stripPrefix("CC").trim)
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

  @annotation.tailrec
  def entryLinesRec(
    currentLine: Option[String],
    linesLeft: Seq[String],
    entryAcc: Seq[String],
    acc: Seq[Seq[String]]
  )
  : Seq[Seq[String]] =
    currentLine match {
      case None       => acc
      case Some(line) => {

        if(isEndLine(line))
          entryLinesRec(
            currentLine = linesLeft.headOption,
            linesLeft   = if(linesLeft.isEmpty) Seq() else linesLeft.tail,
            entryAcc    = Seq(),
            acc         = acc :+ entryAcc
          )
        else
          entryLinesRec(
            currentLine = linesLeft.headOption,
            linesLeft   = if(linesLeft.isEmpty) Seq() else linesLeft.tail,
            entryAcc    = entryAcc :+ line,
            acc         = acc
          )
      }
    }

  def entryLines(lines: Seq[String]): Seq[Seq[String]] =
    entryLinesRec(
      currentLine = lines.headOption,
      linesLeft   = lines.tail,
      entryAcc    = Seq(),
      acc         = Seq()
    )

  def isEndLine(line: String) =
    line.startsWith("//")

  /*
    Again funny file
  */
  def fromLines(lines: Seq[String]): Seq[Entry] =
    entryLines(
      lines.dropWhile( l => l.startsWith("CC") || l.startsWith("//") )
    )
      .map { Entry(_) }

  def validEntriesFromLines(lines: Seq[String]): Seq[Entry] =
    fromLines(lines) filter isValid
}
