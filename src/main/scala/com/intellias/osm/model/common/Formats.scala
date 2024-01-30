package com.intellias.osm.model.common

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.mobility.geo.tools.nds.NdsCoord2D
import play.api.libs.json.{Format, Json}

trait Formats {
  implicit val Wgs84CoordinateFormat: Format[Wgs84Coordinate] = Json.format[Wgs84Coordinate]
  implicit val NdsCoord2DFormat: Format[NdsCoord2D] = Json.format[NdsCoord2D]
}
