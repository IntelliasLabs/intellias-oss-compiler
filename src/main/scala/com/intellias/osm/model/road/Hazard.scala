package com.intellias.osm.model.road

import com.intellias.osm.model.common.DirectionType
import play.api.libs.json.{Format, Json}

case class Hazard(value: String, direction: DirectionType)

object Hazard {
  implicit val format: Format[Hazard] = Json.format[Hazard]
}
