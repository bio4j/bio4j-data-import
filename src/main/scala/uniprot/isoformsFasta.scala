package com.bio4j.release.generic

import ohnosequences.fastarious._, fasta._

case class IsoformFasta(val fa: FASTA.Value) extends AnyVal {

  def proteinID: String =
    // the format of the id is 'sp|${id}|otherstuff'
    fa.getV(fasta.header).id.stripPrefix("sp|").takeWhile(_ != '|')

  def sequence: String =
    fa.getV(fasta.sequence).value
}
