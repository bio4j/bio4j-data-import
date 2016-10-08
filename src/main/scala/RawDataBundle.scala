package com.bio4j.data

import ohnosequences.statika._
import ohnosequences.awstools._, s3._
import java.net.URL
import sys.process._
import better.files._


abstract class GetRawData(
  val urls: Set[URL],
  val baseDirectory: File,
  val gunzip: Boolean
) extends AnyBundle {

  def destination(url: URL): File = (baseDirectory / url.getFile).createIfNotExists()

  def inputStream(url: URL) = {
    val stream = url.openStream
    if (gunzip && url.getFile.endsWith(".gz")) stream.gzipped
    else stream
  }

  def instructions: AnyInstructions = {
    LazyTry {
      for {
        url  <- urls
        inS  <- inputStream(url).autoClosed
        outS <- destination(url).outputStream
      } yield inS pipeTo outS
      // TODO: some retry logic?
    } ->-
    say(s"Files are downloaded to ${baseDirectory}")
  }

}
