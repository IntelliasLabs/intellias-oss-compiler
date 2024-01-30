package com.intellias.osm.ndslive.tools

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.mobility.geo.tools.geotools.GeoTools
import com.intellias.mobility.geo.tools.nds.NdsTools._
import com.intellias.osm.compiler.scale.Scale
import com.intellias.osm.compiler.datasource.osm.OsmNode
import com.typesafe.scalalogging.StrictLogging
import de.rondiplomatico.nds.{NDSTile, WGS84Coordinate}
import nds.core.geometry.{Line2D, Position2D}
import nds.core.types.ScaleRange
import nds.display.types.{DisplayAreaType, DisplayLineType, DisplayPointType}
import org.locationtech.jts.geom.{Coordinate, LineString, LinearRing}

import scala.language.implicitConversions
import scala.reflect.ClassTag

object NdsLiveTools {

  def calculateLengthCm(coordinates: Array[Wgs84Coordinate]): Int =
    (GeoTools.calculateLength(coordinates) * 100).toInt

  def position2D(longitude: Double, latitude: Double, shift: Int = coordRoadShift): Position2D = {
    new Position2D(shift.toByte,
                   dropCoordShiftBits(lon2x(longitude), shift),
                   dropCoordShiftBits(lat2y(latitude), shift))
  }

  def position2DNoShift(longitude: Double, latitude: Double): Position2D =
    position2D(longitude, latitude, 0)


  def line2D(coordinates: Array[Wgs84Coordinate])
            (implicit coordinateShift: Byte): Line2D = {
    new Line2D(
      coordinateShift,
      coordinates.length,
      coordinates.map(c => position2D(longitude = c.lonX, latitude = c.latY, coordinateShift))
    )
  }

  def line2D(coordinates: Array[Coordinate])
            (implicit coordinateShift: Byte): Line2D = {
    line2D(
      coordinates.map(coordinate => Wgs84Coordinate(coordinate.getX, coordinate.getY))
    )
  }

  implicit class CoordinateOps(coordinate: Coordinate) {
    def toNds(implicit coordShift: Byte): Position2D = NdsLiveTools.position2D(coordinate.getX, coordinate.getY, coordShift)
  }

  implicit class LinearRingOps(linearRing: LinearRing) {
    def toNds(implicit coordShift: Byte): Line2D = {
      val coordinates = linearRing.getCoordinates
      line2D(
        if (linearRing.isClosed) {
          coordinates
        } else {
          coordinates :+ coordinates.head
        }
      )
    }
  }

  implicit class LineOps(line: LineString) {
    def toNds(implicit coordShift: Byte): Line2D = {
      line2D(line.getCoordinates)
    }
  }

  implicit class WgsCoordsOps(wgs84: Wgs84Coordinate) {
    def ndsPosition2D: Position2D = {
      new Position2D(coordRoadShiftByte,
                     dropCoordShiftBits(lon2x(wgs84.lonX), coordRoadShift),
                     dropCoordShiftBits(lat2y(wgs84.latY), coordRoadShift))
    }
  }

  implicit class OsmNodeNdsOps(osmNode: OsmNode) {
    def ndsPosition2D: Position2D = position2D(osmNode.longitude, osmNode.latitude)

    def ndsTile(level: Int): NDSTile = {
      new NDSTile(level, new WGS84Coordinate(osmNode.longitude, osmNode.latitude))
    }
  }

  object ConfigurableMetaConverters extends StrictLogging {
    implicit def toScaleRange(scale: Scale): ScaleRange = new ScaleRange(scale.rangeId, scale.min, scale.max)

    implicit def toScaleRanges(scales: Array[Scale])(implicit converter: Scale => ScaleRange): Array[ScaleRange] =
      scales.map(converter)

    sealed trait DisplayObjectTypeWrapper[T] {
      def valueOf(typeName: String): T
    }

    object DisplayObjectTypeWrapper {
      implicit val areaTypeWrapper: DisplayObjectTypeWrapper[DisplayAreaType] = new DisplayObjectTypeWrapper[DisplayAreaType] {
        override def valueOf(typeName: String): DisplayAreaType = DisplayAreaType.valueOf(typeName)
      }
      implicit val lineTypeWrapper: DisplayObjectTypeWrapper[DisplayLineType] = new DisplayObjectTypeWrapper[DisplayLineType] {
        override def valueOf(typeName: String): DisplayLineType = DisplayLineType.valueOf(typeName)
      }
      implicit val pointTypeWrapper: DisplayObjectTypeWrapper[DisplayPointType] = new DisplayObjectTypeWrapper[DisplayPointType] {
        override def valueOf(typeName: String): DisplayPointType = DisplayPointType.valueOf(typeName)
      }
    }

    implicit def parseDisplayObjectTypes[T: ClassTag](typeNames: Array[String])
                                                     (implicit typeWrapper: DisplayObjectTypeWrapper[T]): Array[T] = {
      typeNames.map(typeWrapper.valueOf)
    }
  }
}
