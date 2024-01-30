package com.intellias.osm.compiler.road

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.road.RoadIntersectionProcessor.{aggregateRoads, intersectionNode}
import com.intellias.osm.compiler.topology.TopologiesTable
import com.intellias.osm.model.road.RoadTile
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, SparkSession}

class RoadIntersectionProcessor(commonConf: CommonConfig) extends Processor with StrictLogging {

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val ndsRoadIdWindow = Window.partitionBy(col("tileId")).orderBy("topologyId")

    val navTopologies = data(TopologiesTable)
      .select(
        col("topologyId"),
        col("originId"),
        col("nodes"),
        col("tags"),
        col("tileIds"),
        col("leftAdmin"),
        col("rightAdmin"),
        explode(col("tileIds")) as "tileId"
      ).withColumn("ndsRoadId", row_number().over(ndsRoadIdWindow))


    val navTopoByTile = navTopologies
      .groupBy(col("tileId"))
      .agg(aggregateRoads as "topologies")

    val intersectNodes = navTopologies
      .select(
        col("topologyId"),
        col("tileId") as "topologyTileId",
        explode(col("nodes")) as "node"
      )
      .where(col("node.isIntersectNode") or col("node.isFirstOrLastNode"))
      .withColumn("node", intersectionNode)
      .groupBy(col("node"))
      .agg(collect_set(col("topologyId")) as "topologyIds")
      .groupBy(col("node.nodeTileId"))
      .agg(collect_list(struct(col("node"), col("topologyIds"))) as "intersections")


    val roadTileDs: Dataset[RoadTile] = navTopoByTile
      .join(intersectNodes, col("tileId") === col("nodeTileId"))
      .drop(col("nodeTileId"))
      .as[RoadTile]
      .persist(commonConf.sparkStorageLevel)

    data + (NdsRoadTileTable -> roadTileDs)
  }
}

object RoadIntersectionProcessor {
  def apply(commonConfig: CommonConfig): RoadIntersectionProcessor = new RoadIntersectionProcessor(commonConfig)

  private def aggregateRoads = collect_list(
    struct(
      col("topologyId"),
      col("originId"),
      col("ndsRoadId"),
      col("nodes"),
      col("tags"),
      col("tileIds"),
      col("leftAdmin"),
      col("rightAdmin")
    )
  )

  private def intersectionNode = struct(
    col("node.nodeId"),
    col("topologyTileId") as "nodeTileId",
    col("node.longitude"),
    col("node.latitude"),
    col("node.zLevel"),
    col("node.isVirtual")
  )


}
