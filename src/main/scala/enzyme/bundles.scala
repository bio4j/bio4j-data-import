package com.bio4j.data.enzyme

import com.bio4j.data.bundles._
import java.net.URL
import better.files._

case object bundles {

  case object fileNames {
    val enzyme = "enzyme.dat"
    val enzclass = "enzclass.txt"
  }

  case object rawData extends GetRawData(
    urls = Seq(
      fileNames.enzyme,
      fileNames.enzclass
    ).map { suffix =>
      new URL("ftp", "ftp.ebi.ac.uk", s"/pub/databases/enzyme/release/${suffix}")
    },
    baseDirectory = file"/media/ephemeral0/data/enzyme/",
    gunzip = false
  )()

  case object copyData extends CopyToS3(
    rawData.files,
    s3ReleasesPrefix / "data" / "enzyme" /
  )()

  case object mirroredData extends GetS3Copy(
    copyData,
    file"/media/ephemeral0/data/enzyme/"
  )() {

    val enzyme   = baseDirectory / fileNames.enzyme
    val enzclass = baseDirectory / fileNames.enzclass
  }

}
