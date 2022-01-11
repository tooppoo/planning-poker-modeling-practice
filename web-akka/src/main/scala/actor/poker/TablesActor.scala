package philomagi.dddcj.modeling.planning_poker.web_akka
package actor.poker

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.command.model.Command
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table

import java.util.UUID

object TablesActor {
  trait Message
  object Message {
    case class SetUpTable(owner: Attendance) extends Message

    case class JoinNewly(tableId: Table.Id, newly: Attendance, replyTo: ActorRef[Message]) extends Message
    case object TableFound extends Message
    case object TableNotFound extends Message
  }

  def apply(implicit cd: Command.Dispatcher): Behaviors.Receive[Message] = Behaviors.receive {
    case (ctx, Message.SetUpTable(owner)) =>
      ctx.log.info(s"$owner setup new table")
          val tableId = Table.Id(UUID.randomUUID().toString)
          ctx.spawn(TableActor(tableId, owner), nameOf(tableId))

          Behaviors.same

    case (ctx, Message.JoinNewly(tableId, newly, replyTo)) =>
      ctx.child(nameOf(tableId)) match {
        case Some(child: ActorRef[TableActor.Message]) =>
          child ! TableActor.Message.AcceptNewAttendance(newly)
          replyTo ! Message.TableFound
          Behaviors.same

        case None =>
          ctx.log.error(s"$tableId not found")
          replyTo ! Message.TableNotFound
          Behaviors.same
      }
  }

  private[this] def nameOf(t: Table) = nameOf(t.id)
  private[this] def nameOf(id: Table.Id) = s"table-${id.value}"
}
