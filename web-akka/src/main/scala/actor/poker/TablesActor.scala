package philomagi.dddcj.modeling.planning_poker.web_akka
package actor.poker

import routing.RoutingConfig.Implicits.timeout

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Scheduler}
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.command.model.Command
import philomagi.dddcj.modeling.planning_poker.core.domain.table.model.Table

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

object TablesActor {
  trait Message
  object Message {
    case class SetUpTable(owner: Attendance, replyTo: ActorRef[Table.Id]) extends Message

    case class JoinNewly(tableId: Table.Id, newly: Attendance, replyTo: ActorRef[Message]) extends Message
    case object TableFound extends Message
    case object TableNotFound extends Message

    case class ListTables(replyTo: ActorRef[Seq[Table]]) extends Message
    case class FindTable(id: Table.Id, replyTo: ActorRef[Table]) extends Message
  }

  def apply(implicit cd: Command.Dispatcher): Behaviors.Receive[Message] = Behaviors.receive {
    case (ctx, Message.SetUpTable(owner, replyTo)) =>
      ctx.log.info(s"$owner setup new table")

      val tableId = Table.Id(UUID.randomUUID().toString)
      ctx.spawn(TableActor(tableId, owner), nameOf(tableId))

      replyTo ! tableId

      Behaviors.same

    case (ctx, Message.JoinNewly(tableId, newly, replyTo)) =>
      ctx.child(nameOf(tableId)) match {
        // TODO: 型消去からくるwarningへの対処
        case Some(child: ActorRef[TableActor.Message]) =>
          child ! TableActor.Message.AcceptNewAttendance(newly)
          replyTo ! Message.TableFound
          Behaviors.same

        case _ =>
          ctx.log.error(s"$tableId not found")
          replyTo ! Message.TableNotFound
          Behaviors.same
      }

    case (ctx, Message.ListTables(replyTo)) =>
      implicit val scheduler: Scheduler = schedulerFromActorSystem(ctx.system)
      implicit val ec: ExecutionContext = ctx.executionContext

      val tablesFeatures = ctx.children.foldLeft[Seq[Future[Table]]](Seq.empty) { (xs, act) =>
        act match {
          // TODO: 型消去からくるwarningへの対処
          case a: ActorRef[TableActor.Message] =>
            xs :+ (a ? TableActor.Message.GetTable).mapTo[Table]
          case _ => xs
        }
      }
      Future.sequence(tablesFeatures).map { tables =>
        replyTo ! tables
      }

      Behaviors.same
  }

  private[this] def nameOf(id: Table.Id) = s"table-${id.value}"
}
