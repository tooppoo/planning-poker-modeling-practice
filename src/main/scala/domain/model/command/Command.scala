package philomagi.dddcj.modeling.planning_poker
package domain.model.command

import domain.model.card.Card
import domain.model.member.Member
import domain.model.role.Role
import domain.model.role.Role.{Facilitator, Player}
import domain.model.table.Table
import domain.model.table.Table.CardOnTable

trait Command {
  val actor: Member
  val requiredRole: Option[Role]

  final def run(at: Table): Either[Exception, Table] = requiredRole.fold[Either[Exception, Table]](
     runImpl(at)
  )(
    (required: Role) => if (actor.has(required)) {
      runImpl(at)
    } else {
      Left(
        new Exception(
          s"$actor not has role ${requiredRole.getClass.toString} to run ${getClass.toString}"
        )
      )
    }
  )

  protected[this] def runImpl(at: Table): Either[Exception, Table]
}
object Command {
  def dispatch(command: Command, table: Table): Either[Exception, Table] = command.run(table)

  trait Dispatcher {
    def dispatch(command: Command, table: Table): Either[Exception, Table]
  }
  object Dispatcher {
    object NoPersistenceDispatcher extends Dispatcher {
      override def dispatch(command: Command, table: Table): Either[Exception, Table] = command.run(table)
    }
  }

  object Commands {
    trait FacilitatorCommand extends Command {
      override val requiredRole: Some[Facilitator.type] = Some(Facilitator)
    }
    trait PlayerCommand extends Command {
      val requiredRole: Some[Role] = Some(Player)
    }

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
    case class PutDownCard(actor: Member, card: Card) extends PlayerCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = at.put(CardOnTable(actor, card))
    }

    case class ChangeCardOnTable(actor: Member, card: Card) extends PlayerCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = Right(
        at.replace(CardOnTable(actor, card))
      )
    }
    case class Join(actor: Member) extends Command {
      val requiredRole: Option[Role] = None

      override protected def runImpl(at: Table): Either[Exception, Table] = at.accept(actor)
    }
  }
}
