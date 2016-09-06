name          := "import"
organization  := "bio4j"
description   := "generic bio4j data import"

bucketSuffix  := "era7.com"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq (
  "bio4j"                   % "bio4j"               % "0.12.0-149-gf8523b8",
  "org.scala-lang.modules" %% "scala-xml"           % "1.0.5",
  "org.scala-lang.modules" %% "scala-java8-compat"  % "0.8.0-RC3"
) ++ testDependencies

lazy val testDependencies = Seq (
  "org.scalatest" %% "scalatest" % "2.2.6" % Test
)

dependencyOverrides := Set (
  "org.scala-lang.modules" %% "scala-xml"     % "1.0.5",
  "org.scala-lang"         % "scala-library" % "2.11.8"
)
