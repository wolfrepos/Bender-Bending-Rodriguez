ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.7"
ThisBuild / organization := "io.github.oybek"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.10",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test"
)