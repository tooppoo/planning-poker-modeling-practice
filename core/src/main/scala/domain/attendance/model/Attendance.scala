package philomagi.dddcj.modeling.planning_poker.core
package domain.attendance.model

import domain.role.model.Role

case class Attendance(
                       id: Attendance.Id,
                       name: Attendance.Name,
                       roles: Seq[Role]
                 ) {
  def has(role: Role): Boolean = roles.contains(role)

  def ==(other: Attendance): Boolean = id == other.id
}
object Attendance {
  case class Id(value: String) {
    import lib.extension.StringExtension._

    require(value.nonEmpty, "attendance id must not be empty")
    require(value.nonBlank, "attendance id must not be blank")
    require(value.likeUUID, "attendance id must be valid uuid format")
  }

  case class Name(value: String) {
    import lib.extension.StringExtension._

    require(value.nonEmpty, "attendance name must not be empty")
    require(value.nonBlank, "attendance name must not be blank")
    require(value.within(Name.MaxLength), s"max length of attendance name is ${Name.MaxLength}")
    require(value.notContains(Name.invalidChars), "attendance name include invalid character")
  }
  object Name {
    val MaxLength = 50

    //noinspection DuplicatedCode
    private val invalidChars = Seq(
      '!', '"', '#', '$', '%', '&', '\'', '(', ')', '|',
      '¥', '`', '@', '{', '}', '[', ']', '+', '*',
      '<', '>', '?', '/', '^', '\t', '\\'
    )
  }
}