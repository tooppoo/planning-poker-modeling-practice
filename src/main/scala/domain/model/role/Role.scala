package philomagi.dddcj.modeling.planning_poker
package domain.model.role

sealed trait Role
object Role {
  case object Facilitator extends Role
  case object Player extends Role
  case object Audience extends Role
  case object NewComer extends Role
}
