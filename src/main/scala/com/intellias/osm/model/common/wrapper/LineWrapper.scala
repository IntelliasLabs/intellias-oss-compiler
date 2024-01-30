package com.intellias.osm.model.common.wrapper

import LineWrapper.geometryFactory
import org.locationtech.jts.geom.{GeometryFactory, LineString}

case class LineWrapper(coords: Seq[OrderedCoordinateWrapper]) {
  def toJTS: LineString = {
    geometryFactory.createLineString(coords.sortBy(_.sequence).map(_.toJTS).toArray)
  }

  def isEmpty: Boolean = coords.isEmpty
}

object LineWrapper {
  val geometryFactory = new GeometryFactory()
  def fromJTS(lineString: LineString): LineWrapper = {
    LineWrapper(lineString.getCoordinates.zipWithIndex.map {
      case (coordinate, index) => OrderedCoordinateWrapper(index, coordinate.y, coordinate.x)
    }.toSeq)
  }

  def empty: LineWrapper = new LineWrapper(Seq.empty)
}
