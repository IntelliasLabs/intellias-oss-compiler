package com.intellias.osm.utils

import com.intellias.mobility.geo.tools.jts.GeoFactory
import org.locationtech.jts.geom.{LineString, MultiPolygon}
import org.locationtech.jts.io.WKTReader

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Using}

trait TestUtils {
  def testResourceBase: String
  val wktReader = new WKTReader(GeoFactory)

  def readFile(filePath: String): String = {
    Using(Source.fromFile(new File(testResourceBase, filePath)))(_.mkString) match {
      case Success(value) => value
      case Failure(e)     => throw e
    }
  }

  def toMultiPolygon(wkt: String): MultiPolygon = wktReader.read(wkt).asInstanceOf[MultiPolygon]
  def toLineString(wkt: String): LineString = wktReader.read(wkt).asInstanceOf[LineString]

}
