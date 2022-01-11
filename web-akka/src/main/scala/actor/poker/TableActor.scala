package philomagi.dddcj.modeling.planning_poker.web_akka
package actor.poker

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.command.model.Command
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table

object TableActor {
  trait Message
  object Message {
    case class AcceptNewAttendance(attendance: Attendance) extends Message

    case class GetTable(replyTo: ActorRef[Table]) extends Message
  }

  def apply(id: Table.Id, owner: Attendance)
           (implicit cd: Command.Dispatcher): Behavior[Message] = Behaviors.setup[Message] { ctx =>
    val newTable = Table(id, owner)
    val command = Command.Commands.SetUpNewTable(owner)

    cd.dispatch(command, newTable) match {
      case Right(t) => apply(t)
      case Left(e) =>
        ctx.log.error("failed to create table due to" + e.getMessage)

        Behaviors.unhandled
    }
  }

  def apply(table: Table)(implicit cd: Command.Dispatcher): Behaviors.Receive[Message] = Behaviors.receive {
    case (ctx, Message.AcceptNewAttendance(newly)) =>
      ctx.log.info(s"accept newly $newly")

      cd dispatch(Command.Commands.Join(newly), table) toBehavior ctx

    case (_, Message.GetTable(replyTo)) =>
      replyTo ! table

      Behaviors.same
  }

  private implicit class EitherToBehavior(e: Either[Exception, Table]) {
    def toBehavior(ctx: ActorContext[Message])(implicit cd: Command.Dispatcher): Behavior[Message] = e match {
      case Right(t) => apply(t)
      case Left(e) =>
        ctx.log.error(e.getMessage)
        Behaviors.same
    }
  }
}
