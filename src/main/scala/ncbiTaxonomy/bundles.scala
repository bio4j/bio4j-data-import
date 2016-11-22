package com.bio4j.data.ncbiTaxonomy

import com.bio4j.data.bundles._
import java.net.URL
import better.files._

case object bundles {

  case object rawData extends GetRawData(
    urls = Seq(
      new URL("ftp", "ftp.ncbi.nih.gov", "/pub/taxonomy/taxdump.tar.gz")
    ),
    baseDirectory = file"/media/ephemeral0/ncbiTaxonomy/raw/",
    gunzip = true
  )()

  case object copyData extends CopyToS3(
    rawData.files,
    releasesPrefix / "data" / "ncbiTaxonomy" / 
  )()

  case object mirroredData extends GetS3Copy(
    copyData,
    file"/media/ephemeral0/ncbiTaxonomy/data/"
  )()

}
