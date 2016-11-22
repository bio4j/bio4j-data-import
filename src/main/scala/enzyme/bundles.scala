package com.bio4j.data.enzyme

import com.bio4j.data.bundles._
import java.net.URL
import better.files._

case object bundles {

  case object rawData extends GetRawData(
    urls = Seq(
      "enzyme.dat",
      "enzclass.txt"
    ).map { suffix =>
      new URL("ftp", "ftp.ebi.ac.uk", s"/pub/databases/enzyme/release/${suffix}")
    },
    baseDirectory = file"/media/ephemeral0/enzyme/data/",
    gunzip = false
  )()

  case object copyData extends CopyToS3(
    rawData.files,
    releasesPrefix / "data" / "enzyme" /
  )()

  case object mirroredData extends GetS3Copy(
    copyData,
    file"/media/ephemeral0/enzyme/data/"
  )()

}
