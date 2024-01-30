package com.intellias.osm.compiler

import com.intellias.mobility.geo.tools.jts.GeoFactory
import com.intellias.osm.model.common.wrapper.{LineWrapper, MultiPolygonWrapper}
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf
import org.locationtech.jts.geom.Coordinate

package object geometry {
  object all extends FeatureOps

  val multiPolygonToWktUdf: UserDefinedFunction = udf((area: MultiPolygonWrapper) => area.toJTS.toText)
  val lineToWktUdf: UserDefinedFunction = udf((line: LineWrapper) => line.toJTS.toText)
  val pointToWKTPointString: UserDefinedFunction = udf((lon: Double, lat: Double) =>
    GeoFactory.createPoint(new Coordinate(lon, lat)).toText)
  val isValidAreaPolygonUdf: UserDefinedFunction = udf((area: MultiPolygonWrapper) => area.nonEmpty && area.toJTS.isValid)

}
