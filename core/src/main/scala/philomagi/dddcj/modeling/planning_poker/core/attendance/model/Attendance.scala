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
    require(value.nonEmpty, "attendance id must not be empty")
    require(value.length <= Id.MaxLength, s"max length of attendance id is ${Id.MaxLength}")
  }
  object Id {
    val MaxLength = 100
  }

  case class Name(value: String) {
    require(value.nonEmpty, "attendance name must not be empty")
    require(value.length <= Name.MaxLength, s"max length of attendance name is ${Name.MaxLength}")
    require(notIncludeInvalidCharacters(value), "attendance name include invalid character")

    private def notIncludeInvalidCharacters(v: String): Boolean = {
      val invalidCharactersForMemberName = "[<>\\\\%&$?+*^{}\\[\\]\t]".r

      !invalidCharactersForMemberName.matches(v)
    }
  }
  object Name {
    val MaxLength = 50
  }
}