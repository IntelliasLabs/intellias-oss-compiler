package com.intellias.osm.model.common.wrapper

import org.locationtech.jts.geom.Coordinate
import play.api.libs.json.{Format, Json}

case class CoordinateWrapper(longitude: Double, latitude: Double) {
  def toJTS: Coordinate = new Coordinate(longitude, latitude)
}

object CoordinateWrapper {
  implicit val format: Format[CoordinateWrapper] = Json.format[CoordinateWrapper]
}