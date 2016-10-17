package models

import play.api.libs.json.Json

case class Business(name: String, id: Long = 0L)

object Business {

  implicit val businessFormat = Json.format[Business]
}

