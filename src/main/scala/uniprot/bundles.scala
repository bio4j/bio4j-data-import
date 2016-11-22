package com.bio4j.data.uniprot

import com.bio4j.data.bundles._
import java.net.URL
import better.files._

case object bundles {

  // NOTE: only old releases have a date-tag
  val release = "current_release"

  case object fileNames {
    val sprot  = "uniprot_sprot.dat"   // 517MB gz
    val trembl  = "uniprot_trembl.dat" // 38.9GB gz
  }

  // TODO: probably it's better to make 3 separate data and import bundles
  case object rawData extends GetRawData(
    urls = Seq(
      fileNames.sprot,
      fileNames.trembl
    ).map { suffix =>
      new URL("ftp", "ftp.ebi.ac.uk", s"/pub/databases/uniprot/current_release/knowledgebase/complete/${suffix}.gz")
    },
    baseDirectory = file"/media/ephemeral0/data/enzyme/",
    gunzip = true
  )()

}
