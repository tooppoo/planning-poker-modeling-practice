package philomagi.dddcj.modeling.planning_poker.web_akka
package format

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object RequestFormat {
  object Auth {
    object Register {
      case class Post(name: String)

      object JsonImplicits extends DefaultJsonProtocol with SprayJsonSupport {
        implicit val postFormat: RootJsonFormat[Post] = jsonFormat1(Post)
      }
    }
  }
}
