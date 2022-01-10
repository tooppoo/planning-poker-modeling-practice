package philomagi.dddcj.modeling.planning_poker.web_akka

import actor.user.RegisteredUserActor
import actor.user.RegisteredUserActor.Message.{ListRegisteredUsers, RegisterNewUser}
import format.RequestFormat

import akka.Done
import akka.actor.CoordinatedShutdown
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{post, _}
import akka.util.Timeout
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

object AkkaHttpServer extends App {
  val system = ActorSystem[Nothing](Route(), "system")

  StdIn.readLine()

  system.terminate()

  object Route {
    def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
      import RequestFormat.Auth.Register.JsonImplicits._

      implicit val system: ActorSystem[_] = context.system
      implicit val ec: ExecutionContext = context.executionContext

      val registeredUsers = context.spawn(RegisteredUserActor.apply, "registered-user")

      val route = pathPrefix("api") {
        concat(
          path("user" / "register") {
            concat(
              post {
                entity(as[RequestFormat.Auth.Register.Post]) { post =>
                  system.log.info(s"try register by $post")

                  val message = RegisterNewUser(Attendance.Name(post.name))

                  registeredUsers ! message

                  system.log.info(s"success register by $post")

                  complete(StatusCodes.Accepted)
                }
              },
              get {
                implicit val timeout: Timeout = 5.seconds
                import format.DomainFormat.Implicits._

                val users = (registeredUsers ? ListRegisteredUsers).mapTo[Seq[Attendance]]

                complete(users)
              }
            )
          }
        )
      }

      val binding = Http().newServerAt("localhost", 3333).bind(route)

      system.log.info(s"Server now online. Please navigate to http://localhost:3333/")

      CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceUnbind, "http stop") { () =>
        for {
          _ <- binding.map(_.unbind())
        } yield Done
      }

      Behaviors.receiveSignal[Nothing] {
        case (_, Terminated(_)) =>
          Behaviors.stopped
      }
    }
  }
}
