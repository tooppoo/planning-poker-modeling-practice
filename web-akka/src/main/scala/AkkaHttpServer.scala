package philomagi.dddcj.modeling.planning_poker.web_akka

import actor.poker.{RegisteredUserActor, TablesActor}

import akka.Done
import akka.actor.CoordinatedShutdown
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import philomagi.dddcj.modeling.planning_poker.core.domain.command.model.Command

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object AkkaHttpServer extends App {
  val system = ActorSystem[Nothing](Route(), "system")

  StdIn.readLine()

  system.terminate()

  object Route {
    def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
      implicit val system: ActorSystem[_] = context.system
      implicit val ec: ExecutionContext = context.executionContext

      val attendanceRoute = routing.RegisteredUserRoute(
        context.spawn(RegisteredUserActor.apply, "registered-user"),
        system.log
      )
      val tableRoute = routing.TableRoute(
        context.spawn(TablesActor.apply(Command.Dispatcher.NoPersistenceDispatcher), "table"),
        system.log
      )

      val route = pathPrefix("api" / "poker") {
        concat(
          attendanceRoute.route,
          tableRoute.route
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
