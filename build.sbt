
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.7"
ThisBuild / organization := "io.github.oybek"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.10",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test",

  "io.github.apimorphism" %% "telegramium-core" % "6.53.0",
  "io.github.apimorphism" %% "telegramium-high" % "6.53.0",

  "com.github.pureconfig" %% "pureconfig" % "0.12.3"
)

libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-simple")) }
