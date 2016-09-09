name          := "import"
organization  := "bio4j"
description   := "generic bio4j data import"

bucketSuffix  := "era7.com"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq (
  "bio4j"                   % "bio4j"               % "0.12.0-163-g68b591d",
  "org.scala-lang.modules" %% "scala-xml"           % "1.0.5",
  "org.scala-lang.modules" %% "scala-java8-compat"  % "0.8.0-RC3",
  "com.fasterxml"           % "aalto-xml"           % "1.0.0"
) ++ testDependencies

lazy val testDependencies = Seq (
  "org.scalatest"         %% "scalatest"    % "2.2.6"   % Test,
  "com.github.pathikrit"  %% "better-files" % "2.16.0"  % Test
)

dependencyOverrides := Set (
  "org.scala-lang.modules" %% "scala-xml"     % "1.0.5",
  "org.scala-lang"         % "scala-library" % "2.11.8"
)

wartremoverExcluded ++= Seq(
  baseDirectory.value/"src"/"main"/"scala"/"uniprot"/"uniprotEntry.scala"
)
