package com.intellias.osm.model.road

import play.api.libs.json.{Format, Json}

case class RoadClass (classNum: Short)

object RoadClass {
  implicit val format: Format[RoadClass] = Json.format[RoadClass]
}
