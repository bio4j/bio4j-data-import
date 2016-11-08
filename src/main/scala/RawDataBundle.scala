package com.bio4j.data

import ohnosequences.statika._
import java.net.URL
import sys.process._
import better.files._


abstract class RawDataBundle(
  val url: URL,
  val baseDirectory: File
) extends AnyBundle {

  lazy val destination: File = (baseDirectory / url.getFile).createIfNotExists()

  def instructions: AnyInstructions = {

    lazy val inputStream = {
      val stream = url.openStream
      if (url.getFile.endsWith(".gz")) stream.gzipped
      else stream
    }

    LazyTry {
      for {
        is <- inputStream.autoClosed
        os <- destination.outputStream
      } yield is > os
      // TODO: some retry logic?
    } ->-
    say(s"${url} is downloaded and unpacked to ${destination}")
  }

}
