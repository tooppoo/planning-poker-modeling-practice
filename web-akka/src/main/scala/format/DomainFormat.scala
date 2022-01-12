package philomagi.dddcj.modeling.planning_poker.web_akka
package format

import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.card.model.Card
import philomagi.dddcj.modeling.planning_poker.core.domain.role.model.Role
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table.CardOnTable
import spray.json.DefaultJsonProtocol._
import spray.json.{DeserializationException, JsArray, JsObject, JsString, JsValue, JsonFormat, JsonWriter, RootJsonFormat, enrichAny}

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

    implicit val cardSuiteFormat: RootJsonFormat[Card.Suite] = jsonFormat1(Card.Suite.apply)
    implicit val cardFormat: RootJsonFormat[Card] = jsonFormat1(Card.apply)

    implicit object cardOnTableWriter extends JsonWriter[CardOnTable] {
      override def write(c: CardOnTable): JsValue = Map(
        "card" -> Map(
          "suite" -> c.suite
        ).toJson,
        "owner" -> c.owner.toJson
      ).toJson
    }
    implicit object cardsOnTableWriter extends JsonFormat[Seq[CardOnTable]] {
      override def write(cards: Seq[CardOnTable]): JsValue = cards.map(_.toJson).toJson

      // CardOnTableをjsonで受け付けることは無いが、Marshaller部分でコンパイルエラーになるのでI/Fだけ定義している
      // TODO: レスポンスに使いたいだけだが、write定義のみで済ませられないか？
      override def read(json: JsValue): Seq[CardOnTable] = ???
    }
    implicit val tableIdFormat: RootJsonFormat[Table.Id] = jsonFormat1(Table.Id)
    implicit object TableFormat extends JsonFormat[Table] {
      override def write(t: Table): JsValue = Map(
        "id" -> t.id.toJson,
        "attendance" -> t.attendances.toJson,
        "cards" -> t.cards.toJson,
      ).toJson

      // CardOnTableをjsonで受け付けることは無いが、Marshaller部分でコンパイルエラーになるのでI/Fだけ定義している
      // TODO: レスポンスに使いたいだけだが、write定義のみで済ませられないか？
      override def read(json: JsValue): Table = ???
    }
  }
}
