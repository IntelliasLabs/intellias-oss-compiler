package com.intellias.osm.model.road

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.osm.model.common.DirectionType
import play.api.libs.json.{Format, Json}
import com.intellias.osm.model.common.all._

case class RoadPosition(coordinate: Wgs84Coordinate, directionType: DirectionType = DirectionType.Both)

object RoadPosition {
  implicit val format: Format[RoadPosition] = Json.format[RoadPosition]
}