package philomagi.dddcj.modeling.planning_poker.core
package command.model

import attendance.model.Member
import card.model.Card
import role.model.Role
import role.model.Role.{Facilitator, Player}
import table.model.Table

trait Command {
  val actor: Member
  val requiredRole: Option[Role]

  final def run(at: Table): Either[Exception, Table] =
    requiredRole.fold[Either[Exception, Table]](runImpl(at)) { (required: Role) =>
      if (actor.has(required)) {
        runImpl(at)
      } else {
        Left(
          new Exception(
            s"$actor not has role ${requiredRole.getClass.toString} to run ${getClass.toString}"
          )
        )
      }
    }

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
    trait FreeCommand extends Command {
      override val requiredRole: Option[Role] = None
    }

    case class SetUpNewTable(actor: Member) extends FacilitatorCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = Right(at)
    }

    case class ShowDown(actor: Member) extends FacilitatorCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = Right(at.openCards)
    }

    case class CloseTable(actor: Member) extends FacilitatorCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = Right(at.toEmpty)
    }
    case class PutDownCard(actor: Member, card: Card) extends PlayerCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = at.put(card, by = actor)
    }

    case class ChangeCardOnTable(actor: Member, card: Card) extends PlayerCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = at.replace(card, by = actor)
    }

    case class Join(actor: Member) extends FreeCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = at.accept(actor)
    }
  }
}