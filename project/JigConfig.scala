import org.dddjava.jig.sbt.JigPlugin.autoImport._
import sbt.Project
import sbt.Def
import sbt.Keys.scalaBinaryVersion

case class JigConfig (project: Project) {
  def defaultSettings: Seq[Def.Setting[_]] = {
    val path = project.base.getAbsolutePath

    Def.settings(
      jig / jigProjectPath := path,
      jig / jigOutputDirectoryText := s"$path/target/jig",
      jig / jigDirectoryClasses := s"$path/target/scala-${scalaBinaryVersion.value}/classes",
      jig / jigDirectoryResources := s"$path/target/scala-${scalaBinaryVersion.value}/classes"
    )
  }
}
