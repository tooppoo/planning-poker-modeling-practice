package philomagi.dddcj.modeling.planning_poker.web_akka
package routing

import akka.util.Timeout

import scala.concurrent.duration.DurationInt

object RoutingConfig {
  object Implicits {
    implicit val timeout: Timeout = 5.seconds
  }
}
