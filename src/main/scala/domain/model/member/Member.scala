package philomagi.dddcj.modeling.planning_poker
package domain.model.member

import domain.model.card.Card
import domain.model.command.Command
import domain.model.table.Table
import domain.model.table.Table.CardOnTable

case class Member(
                   id: Member.Id,
                   private val name: Member.Name,
                   private val roles: List[Member.Role]
                 ) {
  def has(role: Member.Role): Boolean = roles.contains(role)
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

      trait FacilitatorCommand extends Command {
        override val requiredRole: Some[Facilitator.type] = Some(Facilitator)
      }

      object Commands {
        case class SetUpNewTable(actor: Member) extends FacilitatorCommand {
          override protected def runImpl(at: Table): Either[Exception, Table] = Right(at)
        }

        case class ShowDown(actor: Member) extends FacilitatorCommand {
          override protected def runImpl(at: Table): Either[Exception, Table] =
            Right(at.withCards(at.cards.map(c => c.open)))
        }

        case class CloseTable(actor: Member) extends FacilitatorCommand {
          override protected def runImpl(at: Table): Either[Exception, Table] = Right(at.toEmpty)
        }
      }
    }

    case object Player extends Role {
      trait PlayerCommand extends Command {
        val requiredRole: Some[Role] = Some(Player)
      }
      object Commands {
        case class PutDownCard(actor: Member, card: Card) extends PlayerCommand {
          override protected def runImpl(at: Table): Either[Exception, Table] = at.put(CardOnTable(actor, card))
        }

        case class ChangeCardOnTable(actor: Member, card: Card) extends PlayerCommand {
          override protected def runImpl(at: Table): Either[Exception, Table] = Right(
            at.replace(CardOnTable(actor, card))
          )
        }
      }
    }

    case object Audience extends Role
    case object NewComer extends Role {
      object Commands {
        case class Join(actor: Member) extends Command {
          val requiredRole: Option[Role] = None

          override protected def runImpl(at: Table): Either[Exception, Table] = at.accept(actor)
        }
      }
    }
  }
}
