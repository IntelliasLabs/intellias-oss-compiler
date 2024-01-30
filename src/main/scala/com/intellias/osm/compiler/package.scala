package com.intellias.osm

import com.intellias.mobility.geo.tools.nds.NdsTileTools
import com.intellias.osm.model.common.wrapper.MultiPolygonWrapper
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

package object compiler {

  case class TileAndLatLong(tileId: Int, longitude: Double, latitude: Double)

  //TODO: move it to proper place.
  def positionToTileIdUdf(tileLevel: Int):  UserDefinedFunction = udf(
    (longitude: Double, latitude: Double) => {
      NdsTileTools.getPackedTileId(tileLevel, longitude, latitude)
    }
  )

  def polygonCentroidToTileIdUdf(tileLevel: Int): UserDefinedFunction = udf(
    (area: MultiPolygonWrapper) => {
      val centroid = area.toJTS.getCentroid
      val tileId = NdsTileTools.getPackedTileId(tileLevel, centroid.getX, centroid.getY)
      TileAndLatLong(tileId, centroid.getX, centroid.getY)
    }
  )

  val createAreaIdUdf: UserDefinedFunction = udf((derivedFrom: String, areaId: Long) => s"osm-$derivedFrom-$areaId")
  val createNodeIdUdf: UserDefinedFunction = udf((nodeId: Long) => s"osm-node-$nodeId")
  val createWayIdUdf: UserDefinedFunction = udf((wayId: Long) => s"osm-way-$wayId")
}
