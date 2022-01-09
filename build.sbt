ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.7"

lazy val root = (project in file("."))
  .settings(
    name := "planning-poker",
    idePackagePrefix := Some("philomagi.dddcj.modeling.planning_poker")
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
libraryDependencies += "org.scalatest" %% "scalatest-funspec" % "3.2.10" % Test
