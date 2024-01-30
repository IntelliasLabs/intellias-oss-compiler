package com.intellias.osm.compiler.topology

import com.intellias.osm.common._
import com.intellias.osm.compiler.datasource.osm.{OsmNodeTable, OsmRelationTable, OsmWayTable}
import com.intellias.osm.compiler.topology.TopologyProcessor._
import com.intellias.osm.model.road.{Relation, Topology, TopologyNode}
import com.intellias.osm.{CommonConfig, compiler}
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import scala.annotation.tailrec
import scala.util.{Success, Try}

/**
 * The topology processor splits OSM ways into links at every intersection.
 */
case class TopologyProcessor(commonConfig: CommonConfig) extends Processor {
  private val toTileId: UserDefinedFunction = compiler.positionToTileIdUdf(commonConfig.tileLevel)

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    data + (TopologiesTable -> build(data))
  }

  def build(data: SharedProcessorData)(implicit spark: SparkSession): Dataset[Topology] = {
    import spark.implicits._

    val ways = data(OsmWayTable)
    val nodes = data(OsmNodeTable)
    val relations = data(OsmRelationTable)

    val roadWays = ways
      .where(isRoadLink(col("tags")))
      .cache()

    val wayNodesWithAttr: DataFrame = roadWays
      .select(
        explode(col("nodeIds")).as("nodeId"),
        getZLevelUdf(col("tags")) as "zLevel"
      )
      .groupBy(col("nodeId"))
      .agg(
        count("nodeId").as("count"),
        max(col("zLevel")) as "zLevel"
      )
      .select(
        col("nodeId"),
        col("zLevel"),
        col("count") > 1 as "isIntersectNode"
      )

    val roadWithNodesIds = roadWays
      .select(
        col("wayId"),
        posexplode(col("nodeIds")) as Array("nodeIdx", "nodeId"),
        isFirstOrLastNode(col("nodeIds"), col("nodeId")) as "isFirstOrLastNode"
      )

    val roadNodes = roadWithNodesIds
      .join(nodes, roadWithNodesIds.col("nodeId") === nodes.col("nodeId"))
      .join(wayNodesWithAttr,
            roadWithNodesIds.col("nodeId") === wayNodesWithAttr.col("nodeId"))
      .drop(nodes.col("nodeId"))
      .drop(wayNodesWithAttr.col("nodeId"))
      .withColumn("nodeTileId", toTileId(col("longitude"), col("latitude")))
      .withColumn("isVirtual", lit(false))
      .groupBy(col("wayId"))
      .agg(aggregateNodes as "nodes")

    val roadWithNodes = roadNodes
      .join(roadWays, roadWays.col("wayId") === roadNodes.col("wayId"))
      .drop(roadWays.col("wayId"))


    val memberRelations = relations
      .select(
        col("relationId"),
        col("type") as "relationType",
        col("tags") as "relationTags",
        explode(col("members")) as "member"
      )
      .withColumn("relationRole", col("member.role"))
      .withColumn("memberId", col("member.id"))
      .groupBy("memberId")
      .agg(aggregateRelations as "relations")


    roadWithNodes
      .join(memberRelations, roadWithNodes.col("wayId") === memberRelations.col("memberId"), "left")
      .as[WayWithNodes]
      .flatMap(wayToTopologies)
      .persist(commonConfig.sparkStorageLevel)
  }
}

object TopologyProcessor {
  case class WayWithNodes(wayId: Long, nodes: Array[TopologyNode], tags: Map[String, String], relations: List[Relation])


  private val isRoadLink = udf((tags: Map[String, String]) =>
    (tags.contains("highway") && !tags.get("highway").contains("rest_area") && !tags.get("area").contains("yes") && !tags.get("highway").contains("proposed"))
    || tags.get("route").contains("ferry")
  )

  def getZLevel(tags: Map[String, String]): Int = tags.get("layer").map(parseLayer).getOrElse(0)

  private def parseLayer(layer: String): Int = {
    Try(layer.toInt) match {
      case Success(value) => value
      case _ => 0
    }
  }

  private val getZLevelUdf = udf((tags: Map[String, String]) => getZLevel(tags))

  private def wayToTopologies(way: WayWithNodes): Seq[Topology] = {
    @tailrec
    def splitNodes(nodes: Array[TopologyNode], acc: Seq[Array[TopologyNode]]): Seq[Array[TopologyNode]] = {
      if (nodes.isEmpty)
        acc
      else {
        val (topN, rem) = nodes.span(n => !n.isIntersectNode || n.isFirstOrLastNode)
        val topoNodes = if (rem.nonEmpty) {
          topN :+ rem.head
        } else topN
        val remaining = if (rem.nonEmpty) {
          rem.head.copy(isFirstOrLastNode = true) +: rem.tail
        } else {
          Array.empty[TopologyNode]
        }
        splitNodes(remaining, acc :+ topoNodes)
      }
    }

    val wayNodes = way.nodes.sortBy(_.nodeIdx)
    val corrected = wayNodes.head.copy(isFirstOrLastNode = true) +: wayNodes.tail.dropRight(1) :+ wayNodes.last
      .copy(isFirstOrLastNode = true)
    val topoNodes = splitNodes(corrected, Seq.empty)

    topoNodes.map(nodes => way.copy(nodes = nodes))

    topoNodes.zipWithIndex.map {
      case (nodes, idx) =>
        Topology(
          topologyId = s"${way.wayId}-$idx",
          originId = way.wayId,
          nodes = nodes,
          tags = way.tags,
          relations = way.relations,
          nodes.map(_.nodeTileId).distinct
        )
    }
  }

  private val isFirstOrLastNode: UserDefinedFunction = udf(
    (nodes: Seq[Long], nodeId: Long) => {
      nodes.indexOf(nodeId) match {
        case idx if idx == 0 || idx == nodes.length - 1 => true
        case _                                          => false
      }
    }
  )

  private def aggregateNodes = collect_list(
    struct(
      col("nodeIdx"),
      col("nodeId"),
      col("longitude"),
      col("latitude"),
      col("isIntersectNode"),
      col("zLevel"),
      col("isFirstOrLastNode"),
      col("nodeTileId"),
      col("tags"),
      col("isVirtual")
    )
  )

  private def aggregateRelations = collect_list(
    struct(
      col("relationId"),
      col("relationType"),
      col("relationRole"),
      col("relationTags")
    )
  )

}
