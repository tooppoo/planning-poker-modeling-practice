package philomagi.dddcj.modeling.planning_poker.web_akka
package routing

import actor.poker.TablesActor
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.slf4j.Logger
import philomagi.dddcj.modeling.planning_poker.core.domain.attendance.model.Attendance
import philomagi.dddcj.modeling.planning_poker.core.domain.table
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import java.util.UUID
import scala.concurrent.ExecutionContext

case class TableRoute
(
  tablesActor: ActorRef[TablesActor.Message],
  logger: Logger
) {
  import TableRoute.Request.Implicits._
  import format.DomainFormat.Implicits._
  import RoutingConfig.Implicits._

  def route(implicit system: ActorSystem[_], ec: ExecutionContext): Route = path("table") {
    concat(
      post {
        entity(as[TableRoute.Request.Create]) { post =>
          logger.info(s"try create new table $post")

          val owner = Attendance.asFacilitator(
            Attendance.Id(UUID.randomUUID().toString),
            Attendance.Name(post.ownerName)
          )

          val message = TablesActor.Message.SetUpTable(owner, _)
          val newTableId = (tablesActor ? message).mapTo[table.model.Table.Id]

          complete(newTableId)
        }
      },
      get {
        pathEnd {
          logger.info(s"list created tables")

          val tables = (tablesActor ? TablesActor.Message.ListTables).mapTo[Seq[table.model.Table]]

          complete(tables)
        }
      }
    )
  }
}
object TableRoute {
  object Request {
    case class Create(ownerName: String)

    object Implicits extends DefaultJsonProtocol with SprayJsonSupport {
      implicit val createFormat: RootJsonFormat[Create] = jsonFormat1(Create)
    }
  }
}
