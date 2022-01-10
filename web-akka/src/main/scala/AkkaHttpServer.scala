package philomagi.dddcj.modeling.planning_poker.web_akka

import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object AkkaHttpServer extends App {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "planning-poker-web-akka")
  implicit val ec: ExecutionContext = system.executionContext

  val route = path("hello") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
    }
  }

  val binding = Http().newServerAt("localhost", 3333).bind(route)

  println(s"Server now online. Please navigate to http://localhost:3333/hello\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  binding
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
