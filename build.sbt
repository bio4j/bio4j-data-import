name          := "import"
organization  := "bio4j"
description   := "generic bio4j data import"

bucketSuffix  := "era7.com"

libraryDependencies ++= Seq (
  "bio4j"                   % "bio4j"     % "0.12.0-143-ge2ff064",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5"
) ++ testDependencies

lazy val testDependencies = Seq (
  "org.scalatest" %% "scalatest" % "2.2.6" % Test
)

dependencyOverrides := Set (
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5"
)
