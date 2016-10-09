package com.bio4j.data

import ohnosequences.statika._
import com.amazonaws.auth._
import ohnosequences.awstools._, s3._
import com.amazonaws.services.s3.transfer._
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


abstract class CopyToS3(
  val files: Seq[File],
  val s3folder: S3Folder
) extends AnyBundle {

  lazy val s3client = S3.create(new InstanceProfileCredentialsProvider())
  lazy val transferManager = new TransferManager(s3client.s3)

  def instructions: AnyInstructions = {

    LazyTry {
      files.foreach { file =>

        val target = s3folder / file.name

        transferManager.upload(
          target.bucket, target.key,
          file.toJava
        ).waitForCompletion
      }

      transferManager.shutdownNow()
    } ->-
    say(s"Files are uploaded to ${s3folder.url}")
  }

}


abstract class GetS3Copy(
  val s3copy: CopyToS3,
  val baseDirectory: File
) extends AnyBundle {

  lazy val s3client = S3.create(new InstanceProfileCredentialsProvider())
  lazy val transferManager = new TransferManager(s3client.s3)

  def instructions: AnyInstructions = {
    LazyTry {
      transferManager.downloadDirectory(
        s3copy.s3folder.bucket, s3copy.s3folder.key,
        baseDirectory.toJava
      ).waitForCompletion

      transferManager.shutdownNow()
    } ->-
    say(s"Files are downloaded to ${baseDirectory}")
  }

}
