package com.bio4j.data.go

import com.bio4j.data.bundles._
import java.net.URL
import better.files._

case object bundles {

  val release: String = "latest"

  case object fileNames {
    val obo = "go_daily-termdb.obo-xml"
  }

  case object rawData extends GetRawData(
    urls = Seq(
      // NOTE: this is daily automatic build, I'm not sure this is the source we want
      new URL("http", "archive.geneontology.org", s"/termdb/${release}/${fileNames.obo}.gz")
    ),
    baseDirectory = file"/media/ephemeral0/data/go/",
    gunzip = true
  )()

  case object copyData extends CopyToS3(
    rawData.files,
    s3ReleasesPrefix / "data" / "go" /
  )()

  case object mirroredData extends GetS3Copy(
    copyData,
    file"/media/ephemeral0/data/go/"
  )() {

    val obo = baseDirectory / fileNames.obo
  }

}
