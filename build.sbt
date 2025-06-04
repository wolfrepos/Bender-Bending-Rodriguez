
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.16"
ThisBuild / organization := "io.github.oybek"

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.12.3",

  "io.github.apimorphism" %% "telegramium-core" % "9.900.0",
  "io.github.apimorphism" %% "telegramium-high" % "9.900.0",

  "org.slf4j" % "slf4j-simple" % "2.0.17",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test",
)

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "versions", "9", "module-info.class") =>
    MergeStrategy.discard
  case x =>
    (assembly / assemblyMergeStrategy).value(x)
}
