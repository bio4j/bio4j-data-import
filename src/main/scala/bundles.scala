package com.bio4j.data

import ohnosequences.statika._
import com.amazonaws.auth._
import ohnosequences.awstools._, s3._
import com.amazonaws.services.s3.transfer._
import java.net.URL
import sys.process._
import better.files._

case object bundles {

  val s3ReleasesPrefix = S3Folder("eu-west-1.raw.bio4j.com", "data/2016_11/")

  abstract class GetRawData(
    val urls: Seq[URL],
    val baseDirectory: File,
    val gunzip: Boolean
  )(deps: AnyBundle*) extends Bundle(deps: _*) {

    def destination(url: URL): File = {
      val urlFile = url.getFile
      val name =
        if (gunzip && urlFile.endsWith(".gz")) urlFile.stripSuffix(".gz")
        else urlFile

      (baseDirectory / name).createIfNotExists()
    }

    lazy val files: Seq[File] = urls.map(destination)

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
  )(deps: AnyBundle*) extends Bundle(deps: _*) {

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
  )(deps: AnyBundle*) extends Bundle(deps: _*) {

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

}
