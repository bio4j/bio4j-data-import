name          := "import"
organization  := "bio4j"
description   := "generic bio4j data import"

bucketSuffix  := "era7.com"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq (
  "bio4j"                   % "bio4j"               % "0.12.0-220-gf5096bb",
  "org.scala-lang.modules" %% "scala-xml"           % "1.0.5",
  "org.scala-lang.modules" %% "scala-java8-compat"  % "0.8.0-RC3",
  "ohnosequences"          %% "fastarious"          % "0.6.0"
) ++ testDependencies

lazy val testDependencies = Seq (
  "org.scalatest"         %% "scalatest"    % "2.2.6"   % Test
)

dependencyOverrides := Set (
  "org.scala-lang.modules" %% "scala-xml"     % "1.0.5",
  "org.scala-lang"         %  "scala-library" % "2.11.8"
)

wartremoverExcluded ++= Seq(
  baseDirectory.value/"src"/"main"/"scala"/"uniprot"/"uniprotEntry.scala"
)
