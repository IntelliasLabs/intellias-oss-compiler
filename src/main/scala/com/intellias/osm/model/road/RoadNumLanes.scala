package com.intellias.osm.compiler.schema.model.road

import play.api.libs.json.{Format, Json}

case class RoadNumLanes (lanesNum: Int)

object RoadNumLanes {
  implicit val format: Format[RoadNumLanes] = Json.format[RoadNumLanes]
}
