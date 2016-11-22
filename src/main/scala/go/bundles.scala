package com.bio4j.data.go

import com.bio4j.data.bundles._
import java.net.URL
import better.files._

case object bundles {

  val release: String = "latest"

  case object rawData extends GetRawData(
    urls = Seq(
      // NOTE: this is daily automatic build, I'm not sure this is the source we want
      new URL("http", "archive.geneontology.org", s"/termdb/${release}/go_daily-termdb.obo-xml.gz")
    ),
    baseDirectory = file"/media/ephemeral0/go/data/",
    gunzip = true
  )()

}
