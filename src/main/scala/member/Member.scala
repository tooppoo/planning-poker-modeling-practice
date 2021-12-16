package philomagi.dddcj.modeling.planning_poker
package member

import card.Card
import command.Command

case class Member(
                   private val id: Member.Id,
                   private val name: Member.Name,
                   private val roles: List[Member.Role]
                 ) {
}
object Member {
  case class Id(value: String) {
    require(value.nonEmpty, "member id must not be empty")
  }
  case class Name(value: String) {
    require(value.nonEmpty, "member name must not be empty")
  }

  sealed trait Role
  object Role {
    case object Facilitator extends Role {
      object Commands {
        case object SetUpNewTable extends Command

        case object ShowDown extends Command

        case object CloseTable extends Command
      }
    }

    case object Player extends Role {
      object Commands {
        case class PutDownCard(player: Member, card: Card) extends Command

        case class ChangeCardOnTable(player: Member, card: Card) extends Command
      }
    }

    case object Audience extends Role
    case object NewComer extends Role {
      object Commands {
        case class Join(member: Member) extends Command
      }
    }
  }
}
