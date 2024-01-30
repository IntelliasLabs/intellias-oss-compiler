package com.intellias.osm.compiler.geometry

import com.intellias.osm.compiler.datasource.osm.{OsmNode, OsmWay}
import com.intellias.osm.model.common.wrapper.WayLineWrapper
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.functions.{col, collect_list, first, lit, map, posexplode, struct, when}

trait LineCollector {
  protected def collectWayLines(osmWays: Dataset[OsmWay], osmNodes: Dataset[OsmNode], withTags: Boolean = false)
                               (implicit spark: SparkSession): Dataset[WayLineWrapper] = {
    import spark.implicits._

    osmWays
      .select(
        col("wayId"),
        posexplode(col("nodeIds")).as(Array("nodeSequence", "wayNodeId")),
        when(lit(withTags), col("tags")).otherwise(map()).as("wayTags")
      )
      .join(osmNodes, col("wayNodeId") === col("nodeId"))
      .select(
        col("wayId") as "wayLineId",
        col("wayTags"),
        col("nodeSequence").as("sequence"),
        col("latitude"),
        col("longitude")
      )
      .groupBy(col("wayLineId"))
      .agg(
        collect_list(struct("sequence", "latitude", "longitude")) as "coordinates",
        first("wayTags") as "tags"
      )
      .as[WayLineWrapper]
  }
}
