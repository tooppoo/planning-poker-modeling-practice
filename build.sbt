
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.7"

ThisBuild / libraryDependencies ++= Dependencies.ScalaTest.dependencies

lazy val root = (project in file("."))
  .aggregate(core, webAkka)
  .settings(
    core / run / aggregate := false,
    ProjectConfig.RootProject.toSettings
  )

lazy val core = (project in file("core"))
  .configure(prj => {
    val coreConf = ProjectConfig.SubProject("core")
    val jigConf = JigConfig(prj)

    prj
      .settings(
        coreConf.toSettings,
        jigConf.defaultSettings,
        jig / jigPatternDomain := s".+\\.core\\..+"
      )
  })

lazy val webAkka = (project in file("web-akka"))
  .configure(prj => {
    val webAkkaConf = ProjectConfig.SubProject("web-akka", "web_akka")
    val jigConf = JigConfig(prj)

    prj.settings(
      webAkkaConf.toSettings,
      jigConf.defaultSettings,
      jig / jigPatternDomain := ".+\\.web_akka\\..+"
    )
  })
  .settings(
    libraryDependencies ++= Dependencies.Akka.dependencies
  )
  .dependsOn(core)
