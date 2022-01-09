package philomagi.dddcj.modeling.planning_poker.core
package attendance.model

import role.model.Role

case class Attendance(
                       id: Attendance.Id,
                       private val name: Attendance.Name,
                       private val roles: List[Role]
                 ) {
  def has(role: Role): Boolean = roles.contains(role)
}
object Attendance {
  case class Id(value: String) {
    import philomagi.dddcj.modeling.planning_poker.core.attendance.lib.extension.StringExtension._

    require(value.nonEmpty, "attendance id must not be empty")
    require(value.nonBlank, "attendance id must not be blank")
    require(value.within(Id.MaxLength), s"max length of attendance id is ${Id.MaxLength}")
    require(value.matches("^[a-zA-Z0-9_-]+"), "id contains also invalid characters")
  }
  object Id {
    val MaxLength = 100
  }

  case class Name(value: String) {
    import philomagi.dddcj.modeling.planning_poker.core.attendance.lib.extension.StringExtension._

    require(value.nonEmpty, "attendance name must not be empty")
    require(value.nonBlank, "attendance name must not be blank")
    require(value.within(Name.MaxLength), s"max length of attendance name is ${Name.MaxLength}")
    require(
      value.notContains(Seq(
        '<', '>', '%', '&', '?', '!', '+', '*', '/',
        '^', '{', '}', '[', ']', '\t', '\\'
      )),
      "attendance name include invalid character"
    )
  }
  object Name {
    val MaxLength = 50
  }
}