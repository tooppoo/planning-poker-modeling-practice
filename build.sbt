ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.7"

lazy val root = (project in file("."))
  .settings(
    name := "planning-poker",
    idePackagePrefix := Some("philomagi.dddcj.modeling.planning_poker")
  ).dependsOn(jigRepo)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
lazy val jigRepo = RootProject(uri("git://github.com/yoshiyoshifujii/sbt-jig.git#v2021.12.1"))
