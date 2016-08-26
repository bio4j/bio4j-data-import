name          := "bio4j-data-import"
organization  := "bio4j"
description   := "generic bio4j data import"

bucketSuffix  := "era7.com"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
  "org.scalatest" %% "scalatest" % "2.2.6" % Test
)
