package philomagi.dddcj.modeling.planning_poker.web_akka
package format

import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.role.model.Role
import spray.json.DefaultJsonProtocol._
import spray.json.{DeserializationException, JsArray, JsObject, JsString, JsValue, RootJsonFormat, enrichAny}

object DomainFormat {
  object Implicits {
    implicit object RoleFormat extends RootJsonFormat[Role] {
      private val playerLabel = "player"
      private val facilitatorLabel = "facilitator"
      private val audienceLabel = "audience"
      private val newComerLabel = "new-comer"

      override def read(json: JsValue): Role = json match {
        case JsObject(m) => m.get("id") match {
          case Some(value) => value match {
            case JsString(label) if label == playerLabel => Role.Player
            case JsString(label) if label == facilitatorLabel => Role.Facilitator
            case JsString(label) if label == audienceLabel => Role.Audience
            case JsString(label) if label == newComerLabel => Role.NewComer
            case _ => throw DeserializationException("Read Role")
          }
          case _ => throw DeserializationException("Read Role")
        }
        case _ => throw DeserializationException("Read Role")
      }

      override def write(role: Role): JsValue = role match {
        case Role.Player => Map(
          "id" -> playerLabel
        ).toJson
        case Role.Facilitator => Map(
          "id" -> facilitatorLabel
        ).toJson
        case Role.Audience => Map(
          "id" -> audienceLabel
        ).toJson
        case Role.NewComer => Map(
          "id" -> newComerLabel
        ).toJson
      }
    }
    implicit val attendanceIdFormat: RootJsonFormat[Attendance.Id] = jsonFormat1(Attendance.Id)
    implicit val attendanceNameFormat: RootJsonFormat[Attendance.Name] = jsonFormat1(Attendance.Name.apply)

    implicit object attendanceFormat extends RootJsonFormat[Attendance] {
      override def read(json: JsValue): Attendance =
        json.asJsObject.getFields("id", "name", "roles") match {
          case Seq(JsString(id), JsString(name), JsArray(roles)) =>
            Attendance(
              Attendance.Id(id),
              Attendance.Name(name),
              roles.map(_.convertTo[Role])
            )
          case _ => throw DeserializationException("Read Attendance")
        }

      override def write(at: Attendance): JsValue = Map(
        "id" -> at.id.value.toJson,
        "name" -> at.name.value.toJson,
        "roles" -> at.roles.map(_.toJson).toJson
      ).toJson
    }
    implicit val attendanceListFormat: RootJsonFormat[Seq[Attendance]] = immSeqFormat[Attendance]
  }
}
