package com.intellias.osm.compiler.geometry

import com.intellias.mobility.geo.tools.jts.GeoFactory
import com.intellias.mobility.geo.tools.jts.all._
import com.intellias.osm.model.common.wrapper.{MultiPolygonWrapper, OrderedCoordinateWrapper}
import com.typesafe.scalalogging.StrictLogging
import org.locationtech.jts.geom._
import org.locationtech.jts.linearref.LinearGeometryBuilder
import org.locationtech.jts.operation.polygonize.Polygonizer

import scala.jdk.CollectionConverters._

trait AreaCollector extends StrictLogging with Serializable {

  protected def createPolygon(pieces: Seq[(Seq[OrderedCoordinateWrapper], String)]): MultiPolygonWrapper = {
    val (outer, inner) = pieces.foldLeft(
      (
        Seq.empty[Seq[OrderedCoordinateWrapper]],
        Seq.empty[Seq[OrderedCoordinateWrapper]]
      )) {
      case ((outer, inner), piece) if piece._2 == "outer" => (outer :+ piece._1, inner)
      case ((outer, inner), piece) if piece._2 == "inner" => (outer, inner :+ piece._1)
    }

    if (outer.nonEmpty) {
      val polygonExtractor = new Polygonizer(true)
      polygonExtractor.add(createMultiline(outer))
      polygonExtractor.add(createMultiline(inner))

      val polygons = polygonExtractor.getPolygons.asScala.map(_.asInstanceOf[Polygon]).map(_.mergeHoles)
      MultiPolygonWrapper.fromJTS(polygons)
    } else {
      //There's no sense in creating geometry without outer lines.
      MultiPolygonWrapper(Seq.empty)
    }
  }

  protected def createMultiline(pieces: Seq[Seq[OrderedCoordinateWrapper]]): MultiLineString = {
    val boundaries = pieces
      .map(crateLine)
      .flatMap {
        case boundaryLine: LineString => Some(boundaryLine)
        case boundaryGeometry @ _ =>
          logger.error(s"Piece of admin area boundary is represented with a non-linear geometry: ${boundaryGeometry.getGeometryType}")
          None
      }
      .toArray
    GeoFactory.createMultiLineString(boundaries)
  }

  private def crateLine(boundaryLineCoordinates: Seq[OrderedCoordinateWrapper]) = {
    val linearGeometryBuilder = new LinearGeometryBuilder(GeoFactory)
    boundaryLineCoordinates
      .sortBy(_.sequence)
      .map(_.toJTS)
      .foreach(linearGeometryBuilder.add)
    linearGeometryBuilder.endLine()
    linearGeometryBuilder.getGeometry
  }
}
