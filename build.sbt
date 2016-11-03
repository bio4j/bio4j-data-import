name          := "release.generic"
organization  := "bio4j"
description   := "generic import code for data releases"

bucketSuffix  := "era7.com"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq (
  "bio4j"                   % "bio4j"               % "0.12.0-227-g60cce98",
  "bio4j"                  %% "data-uniprot"        % "0.1.1",
  "org.scala-lang.modules" %% "scala-xml"           % "1.0.5",
  "org.scala-lang.modules" %% "scala-java8-compat"  % "0.8.0-RC3",
  "ohnosequences"          %% "fastarious"          % "0.6.0"
) ++ testDependencies

lazy val testDependencies = Seq (
  "org.scalatest"         %% "scalatest"    % "2.2.6"   % Test
)

dependencyOverrides := Set (
  "org.scala-lang.modules" %% "scala-xml"     % "1.0.5",
  "org.scala-lang"         %  "scala-library" % "2.11.8",
  "com.github.pathikrit"   %% "better-files"  % "2.13.0"
)

wartremoverExcluded ++= Seq(
  baseDirectory.value/"src"/"main"/"scala"/"uniprot"/"uniprot.scala",
  baseDirectory.value/"src"/"test"/"scala"/"ncbiTaxonomy.scala"
)
