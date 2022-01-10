package philomagi.dddcj.modeling.planning_poker.web_akka

import actor.user.RegisteredUserActor
import actor.user.RegisteredUserActor.Message
import actor.user.RegisteredUserActor.Message.{ListRegisteredUsers, RegisterNewUser}
import format.RequestFormat

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

object AkkaHttpServer extends App {
  implicit val system: ActorSystem[Message]
    = ActorSystem(RegisteredUserActor.apply, "planning-poker-web-akka")
  implicit val ec: ExecutionContext = system.executionContext

  import RequestFormat.Auth.Register.JsonImplicits._

  val route = Route.seal(pathPrefix("api") {
    concat(
      path("user" / "register") {
        concat(
          post {
            entity(as[RequestFormat.Auth.Register.Post]) { post =>
              system.log.info(s"try register by $post")

              val message = RegisterNewUser(Attendance.Name(post.name))

              system ! message

              system.log.info(s"success register by $post")

              complete(StatusCodes.Accepted)
            }
          },
          get {
            implicit val timeout: Timeout = 5.seconds
            import format.DomainFormat.Implicits._

            val users = (system ? ListRegisteredUsers).mapTo[Seq[Attendance]]

            complete(users)
          }
        )
      }
    )
  })

  val binding = Http().newServerAt("localhost", 3333).bind(route)

  system.log.info(s"Server now online. Please navigate to http://localhost:3333/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  binding
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
