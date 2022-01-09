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
  }
  case class Name(value: String) {
    require(value.nonEmpty, "member name must not be empty")
  }
}
