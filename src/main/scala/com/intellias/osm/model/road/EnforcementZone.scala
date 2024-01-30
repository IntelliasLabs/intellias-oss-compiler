package com.intellias.osm.model.road

import com.intellias.osm.model.common.DirectionType
import play.api.libs.json.{Format, Json}

case class EnforcementZone(enforcementType: EnforcementType, lengthInMeters: Int, direction: DirectionType)

object EnforcementZone {
  implicit val format: Format[EnforcementZone] = Json.format[EnforcementZone]
}
