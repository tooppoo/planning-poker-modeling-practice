package philomagi.dddcj.modeling.planning_poker.web_akka
package routing

import actor.poker.RegisteredUserActor
import actor.poker.RegisteredUserActor.Message.{ListRegisteredAttendances, RegisterNewAttendance}
import format.RequestFormat

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, path, post}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import org.slf4j.Logger
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

case class RegisteredUserRoute
(
  actor: ActorRef[RegisteredUserActor.Message],
  logger: Logger
) {
  import RequestFormat.Auth.Register.JsonImplicits._

  def route(implicit system: ActorSystem[_], ec: ExecutionContext): Route = path("user") {
    concat(
      post {
        entity(as[RequestFormat.Auth.Register.Post]) { post =>
          logger.info(s"try register by $post")

          val message = RegisterNewAttendance(Attendance.Name(post.name))

          actor ! message

          logger.info(s"success register by $post")

          complete(StatusCodes.Accepted)
        }
      },
      get {
        implicit val timeout: Timeout = 5.seconds
        import format.DomainFormat.Implicits._

        val users = (actor ? ListRegisteredAttendances).mapTo[Seq[Attendance]]

        complete(users)
      }
    )
  }
}
