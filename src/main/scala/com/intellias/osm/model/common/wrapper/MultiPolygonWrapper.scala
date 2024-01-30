package com.intellias.osm.model.common.wrapper

import MultiPolygonWrapper.geometryFactory
import org.locationtech.jts.geom.{Geometry, GeometryFactory, MultiPolygon, Polygon}
import play.api.libs.json.{Format, Json}

case class MultiPolygonWrapper(polygons: Seq[PolygonWrapper]) {
  def toJTS: MultiPolygon = {
    geometryFactory.createMultiPolygon(
      polygons.map(_.toJTS).toArray
    )
  }

  def isEmpty: Boolean = polygons.forall(_.isEmpty)
  def nonEmpty: Boolean = !isEmpty
}

object MultiPolygonWrapper {
  val geometryFactory = new GeometryFactory()

  private def polygonToCoordinates(polygon: Polygon): PolygonWrapper = {
    val outerCoords = polygon.getExteriorRing.getCoordinates.zipWithIndex.map {
      case (coordinate, index) => OrderedCoordinateWrapper(index, coordinate.y, coordinate.x)
    }.toSeq

    val holes = (0 until polygon.getNumInteriorRing).map(i => polygon.getInteriorRingN(i).getCoordinates)
      .map(coords => coords.zipWithIndex.map {
        case (coordinate, index) => OrderedCoordinateWrapper(index, coordinate.y, coordinate.x)
      }.toSeq
      )

    PolygonWrapper(outerCoords, holes = holes)
  }

  def fromJTS(multiPolygon: MultiPolygon): MultiPolygonWrapper = {
    MultiPolygonWrapper(
      Range(0, multiPolygon.getNumGeometries)
        .map(multiPolygon.getGeometryN)
        .map(p => polygonToCoordinates(p.asInstanceOf[Polygon]))
    )
  }

  def fromJTS(polygons: Iterable[Polygon]): MultiPolygonWrapper = {
    MultiPolygonWrapper(
      polygons.map(polygonToCoordinates).toSeq
    )
  }

  def apply(): MultiPolygonWrapper = MultiPolygonWrapper(polygons = Seq.empty)

  def fromJTS(geometry: Geometry): Either[IllegalGeometryException, MultiPolygonWrapper] = geometry match {
    case geometry: Geometry if geometry.isEmpty => Right(apply())
    case polygon: Polygon => Right(fromJTS(Seq(polygon)))
    case multiPolygon: MultiPolygon => Right(fromJTS(multiPolygon))
    case other => Left(IllegalGeometryException(message = s"Invalid geometry of type: ${other.getClass} for converting to MultiPolygonWrapper"))
  }

  implicit val format: Format[MultiPolygonWrapper] = Json.format[MultiPolygonWrapper]
}

case class PolygonWrapper(outer: Seq[OrderedCoordinateWrapper], holes: Seq[Seq[OrderedCoordinateWrapper]]) {
  def isEmpty: Boolean = outer.isEmpty
  def nonEmpty: Boolean = !isEmpty

  def toJTS: Polygon = {
    val outerRing = geometryFactory.createLinearRing(outer.map(_.toJTS).toArray)
    val innerRings = holes.map(hole => geometryFactory.createLinearRing(hole.map(_.toJTS).toArray)).toArray
    geometryFactory.createPolygon(outerRing, innerRings)
  }
}

object PolygonWrapper {
  implicit val format: Format[PolygonWrapper] = Json.format[PolygonWrapper]
}