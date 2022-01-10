import sbt._

object Dependencies {
  object ScalaTest {
    val version = "3.2.10"

    val dependencies = Seq(
      "org.scalatest" %% "scalatest" % version % Test,
      "org.scalatest" %% "scalatest-funspec" % "3.2.10" % Test
    )
  }

  object Akka {
    val version = "2.6.18"

    val dependencies = Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % version,
      "com.typesafe.akka" %% "akka-stream" % version,
      "com.typesafe.akka" %% "akka-http" % "10.2.6",
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % version % Test,
    )
  }
}
