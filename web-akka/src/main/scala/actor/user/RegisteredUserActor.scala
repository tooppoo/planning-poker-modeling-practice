package philomagi.dddcj.modeling.planning_poker.web_akka
package actor.user

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance

import java.util.UUID

object RegisteredUserActor {
  sealed trait Message
  object Message {
    case class RegisterNewUser(name: Attendance.Name) extends Message

    case class ListRegisteredUsers(replyTo: ActorRef[Seq[Attendance]]) extends Message
  }

  def apply: Behaviors.Receive[Message] = apply(Seq.empty)
  def apply(registered: Seq[Attendance]): Behaviors.Receive[Message] = Behaviors.receive {
    case (ctx, Message.RegisterNewUser(name)) =>
      ctx.log.info(s"register new user $name")

      val newlyRegistered = Attendance(
        Attendance.Id(UUID.randomUUID().toString),
        name,
        Seq.empty
      )

      apply(registered :+ newlyRegistered)
    case (ctx, Message.ListRegisteredUsers(replyTo)) =>
      ctx.log.info(s"return $registered")

      replyTo ! registered

      Behaviors.same
  }
}
