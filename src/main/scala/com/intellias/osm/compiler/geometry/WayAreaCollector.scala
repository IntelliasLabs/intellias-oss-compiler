package com.intellias.osm.compiler.geometry

import com.intellias.osm.common.SharedProcessorData
import com.intellias.osm.compiler.datasource.osm.{OsmNodeTable, OsmWay}
import com.intellias.osm.compiler.geometry.AreaPolygonCollector.WayType
import com.intellias.osm.model.common.wrapper.OrderedCoordinateWrapper
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, SparkSession}

class WayAreaCollector(data: SharedProcessorData)(implicit spark: SparkSession) extends AreaCollector with LineCollector {

  def collect(ways: Dataset[OsmWay], withTags: Boolean = false): Dataset[AreaPolygon] = {
    import spark.implicits._

    collectWayLines(ways, data(OsmNodeTable), withTags)
      .select(
        col("wayLineId") as "areaId",
        collectBoundaryFromPieces(col("coordinates")) as "area",
        col("tags"),
        lit(WayType) as "derivedFrom"
      )
      .as[AreaPolygon]

  }

  private val collectBoundaryFromPieces: UserDefinedFunction = udf((line: Seq[OrderedCoordinateWrapper]) =>
    createPolygon(Seq((line,"outer")))
  )
}
