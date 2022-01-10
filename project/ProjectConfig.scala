
import sbt.{Def, KeyRanks, Keys}
import Keys._
import sbtide.Keys.idePackagePrefix

import scala.language.implicitConversions

trait ProjectConfig {
  val projectName: String

  val packagePrefix: String

  def toSettings: Seq[Def.Setting[_]]
}

object ProjectConfig {
  object RootProject extends ProjectConfig {
    val projectName: String = "planning-poker"
    val packagePrefix: String = "philomagi.dddcj.modeling.planning_poker"

    def toSettings: Seq[Def.Setting[_]] = Def.settings(
      name := projectName,
    )
  }

  class SubProject private (
                    private val subProjectName: String,
                    private val subPackageName: String
                  ) extends ProjectConfig {
    override val projectName: String = s"${RootProject.projectName}-$subProjectName"

    override val packagePrefix: String = s"${RootProject.packagePrefix}.$subPackageName"

    override def toSettings: Seq[Def.Setting[_]] = Def.settings(
      name := projectName,
      idePackagePrefix.withRank(KeyRanks.Invisible) := Some(packagePrefix),
    )
  }
  object SubProject {
    def apply(subName: String) = new SubProject(subName, subName)
    def apply(subProjectName: String, subPackageName: String) = new SubProject(subProjectName, subPackageName)
  }
}
