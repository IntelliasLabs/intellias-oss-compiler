package com.intellias.osm.model.road

import com.intellias.osm.model.common.Side
import play.api.libs.json.{Format, Json}

case class ParkingType(side: Side, key: Option[String], value: String, condition: Option[String])

object ParkingType {
  implicit val format: Format[ParkingType] = Json.format[ParkingType]
}