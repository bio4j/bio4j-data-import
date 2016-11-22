package com.bio4j.data.uniref

import com.bio4j.data.bundles._
import java.net.URL
import better.files._

case object bundles {

  // NOTE: only old releases have a date-tag
  val release = "current_release"

  case object fileNames {
    val uniref50  = "uniref50.xml"   //  8.5GB gz
    val uniref90  = "uniref90.xml"   // 15.4GB gz
    val uniref100 = "uniref100.xml"  // 27.7GB gz
  }

  // TODO: probably it's better to make 3 separate data and import bundles
  case object rawData extends GetRawData(
    urls = Seq(
      fileNames.uniref50,
      fileNames.uniref90,
      fileNames.uniref100
    ).map { suffix =>
      new URL("ftp", "ftp.ebi.ac.uk", s"/pub/databases/uniprot/current_release/uniref/${suffix}/${suffix}.xml.gz")
    },
    baseDirectory = file"/media/ephemeral0/data/enzyme/",
    gunzip = true
  )()

}
