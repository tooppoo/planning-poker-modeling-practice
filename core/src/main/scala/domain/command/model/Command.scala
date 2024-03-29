package philomagi.dddcj.modeling.planning_poker.core
package domain.command.model

import domain.attendance.model.Attendance
import domain.card.model.Card
import domain.role.model.Role
import domain.role.model.Role.{Facilitator, Player}
import domain.table.model.Table

trait Command {
  val actor: Attendance
  val requiredRole: Option[Role]

  protected[command] final def run(at: Table): Either[Exception, Table] = {
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
  }

  protected[this] def runImpl(at: Table): Either[Exception, Table]
}
object Command {
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
      override val requiredRole: Option[Role] = Some(Facilitator)
    }
    trait PlayerCommand extends Command {
      override val requiredRole: Option[Role] = Some(Player)
    }
    trait FreeCommand extends Command {
      override val requiredRole: Option[Role] = None
    }

    case class SetUpNewTable(actor: Attendance) extends FacilitatorCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = Right(at)
    }
    case class ShowDown(actor: Attendance) extends FacilitatorCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = Right(at.openCards)
    }
    case class CloseTable(actor: Attendance) extends FacilitatorCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = Right(at.toEmpty)
    }

    case class PutDownCard(actor: Attendance, card: Card) extends PlayerCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = at.put(card, by = actor)
    }

    case class Join(actor: Attendance) extends FreeCommand {
      override protected def runImpl(at: Table): Either[Exception, Table] = at.accept(actor)
    }
  }
}