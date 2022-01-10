package philomagi.dddcj.modeling.planning_poker.core
package domain.role.model

sealed trait Role
object Role {
  case object Facilitator extends Role
  case object Player extends Role
  case object Audience extends Role
  case object NewComer extends Role
}