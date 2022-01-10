
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.7"

ThisBuild / libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
ThisBuild / libraryDependencies += "org.scalatest" %% "scalatest-funspec" % "3.2.10" % Test

lazy val root = (project in file("."))
  .settings(
    name := "planning-poker"
  )
  .aggregate(core, webAkka)

lazy val core = (project in file("core"))
  .configure(prj => {
    val path = prj.base.getAbsolutePath

    prj.settings(
      jig / jigProjectPath := path,
      jig / jigOutputDirectoryText := s"$path/target/jig",
      jig / jigDirectoryClasses := s"$path/target/scala-${scalaBinaryVersion.value}/classes",
      jig / jigDirectoryResources := s"$path/target/scala-${scalaBinaryVersion.value}/classes",
      jig / jigPatternDomain := s".+\\.core\\..+"
    )
  })

lazy val webAkka = (project in file("web-akka"))
  .dependsOn(core)
