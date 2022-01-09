package philomagi.dddcj.modeling.planning_poker
package domain.model.member

import domain.model.role.Role

case class Member(
                   id: Member.Id,
                   private val name: Member.Name,
                   private val roles: List[Role]
                 ) {
  def has(role: Role): Boolean = roles.contains(role)
}
object Member {
  case class Id(value: String) {
    require(value.nonEmpty, "member id must not be empty")
    require(value.length <= Id.MaxLength, s"max length of member id is ${Id.MaxLength}")
  }
  object Id {
    val MaxLength = 100
  }

  case class Name(value: String) {
    require(value.nonEmpty, "member name must not be empty")
    require(value.length <= Name.MaxLength, s"max length of member name is ${Name.MaxLength}")
    require(notIncludeInvalidCharacters(value), "member name include invalid character")

    private def notIncludeInvalidCharacters(v: String): Boolean = {
      val invalidCharactersForMemberName = "[<>\\\\%&$?+*^{}\\[\\]\t]".r

      !invalidCharactersForMemberName.matches(v)
    }
  }
  object Name {
    val MaxLength = 50
  }
}
