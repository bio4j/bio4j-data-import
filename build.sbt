Nice.scalaProject

name          := "bio4j-data-import"
organization  := "bio4j"
description   := "bio4j-data-import project"

bucketSuffix  := "era7.com"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % Test

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1"
)

import ScalaxbKeys._

scalaxbSettings
packageName in scalaxb in Compile := "com.bio4j.xsd"
protocolPackageName in scalaxb in Compile := Some("scalaxb.protocol")
sourceGenerators in Compile <+= scalaxb in Compile
sourceManaged in (Compile, scalaxb) := baseDirectory.value/"src"/"generated"/"scala"
dispatchVersion in scalaxb in Compile := "0.11.3"

wartremoverExcluded ++= Seq(
  baseDirectory.value/"src"/"generated"/"scala"/"scalaxb"/"protocol"/"xmlprotocol.scala",
  baseDirectory.value/"src"/"generated"/"scala"/"scalaxb"/"scalaxb.scala"
)
