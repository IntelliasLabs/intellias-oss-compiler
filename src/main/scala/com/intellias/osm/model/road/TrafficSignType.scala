package com.intellias.osm.model.road

import com.intellias.osm.model.common.DirectionType
import play.api.libs.json.{Format, Json}

case class TrafficSignType(direction: DirectionType, signType: String, condition: Option[String])

object TrafficSignType {
  implicit val format: Format[TrafficSignType] = Json.format[TrafficSignType]
}
