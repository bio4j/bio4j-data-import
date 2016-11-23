package com.bio4j.data.ncbiTaxonomy

import com.bio4j.data.bundles._
import java.net.URL
import better.files._

case object bundles {

  case object fileNames {
    val nodes = "nodes.dmp"
    val names = "names.dmp"
  }

  case object rawData extends GetRawData(
    urls = Seq(
      new URL("ftp", "ftp.ncbi.nih.gov", "/pub/taxonomy/taxdump.tar.gz")
    ),
    baseDirectory = file"/media/ephemeral0/data/ncbiTaxonomy/",
    gunzip = true
  )() {

    val nodes = baseDirectory / "taxdump" / fileNames.nodes
    val names = baseDirectory / "taxdump" / fileNames.names
  }

  case object copyData extends CopyToS3(
    Seq(rawData.nodes, rawData.names),
    s3ReleasesPrefix / "ncbiTaxonomy" /
  )()

  case object mirroredData extends GetS3Copy(
    copyData,
    file"/media/ephemeral0/data/ncbiTaxonomy/"
  )() {

    val nodes = baseDirectory / fileNames.nodes
    val names = baseDirectory / fileNames.names
  }

}
