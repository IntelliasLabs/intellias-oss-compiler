package com.intellias.osm.model.common.wrapper

import org.locationtech.jts.geom.Coordinate
import play.api.libs.json.{Format, Json}

case class OrderedCoordinateWrapper(sequence: Int, latitude: Double, longitude: Double) {
  def toJTS: Coordinate = new Coordinate(longitude, latitude)
}
object OrderedCoordinateWrapper {
  implicit val format: Format[OrderedCoordinateWrapper] = Json.format[OrderedCoordinateWrapper]
}
