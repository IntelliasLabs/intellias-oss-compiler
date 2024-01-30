package com.intellias.osm.model.road

import com.intellias.osm.model.common.DirectionType
import play.api.libs.json.{Format, Json}

case class DeniedAccess(vehicle: Seq[VehicleType], direction: DirectionType, condition: Option[String])

object DeniedAccess {
  implicit val format: Format[DeniedAccess] = Json.format[DeniedAccess]
}
