package com.intellias.osm.compiler.road

import com.intellias.osm.common._
import com.intellias.osm.compiler.datasource.osm._
import com.intellias.osm.compiler.road.rules.RoadRulesExtendedExtractor
import com.intellias.osm.compiler.road.rules.range._
import com.intellias.osm.compiler.topology.TopologiesTable
import com.intellias.osm.model.road.{EnforcementZone, Topology}
import com.intellias.osm.tools.JsonMapperProvider
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import play.api.libs.json.Json

object RoadRulesProcessorWithRelations extends Processor with JsonMapperProvider with StrictLogging with Serializable {
  private val extractors: Seq[RoadRulesExtendedExtractor[EnforcementZone]] = Seq(EnforcementExtractor)
  private val extractorMap                                                 = extractors.map(extr => (extr.tag, extr)).toMap

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val relations = data(OsmRelationTable)
      .filter(relation => extractors.exists(_.filterRelation(relation)))

    //relations needs some nodes, which can be out of topology list
    val relationMembers = relations.flatMap(_.members)

    val filteredNodes = data(OsmNodeTable)
      .alias("nodes")
      .join(relationMembers, $"nodes.nodeId" === relationMembers("id") && relationMembers("type") === "Node", "left_outer")
      .filter(col("id").isNotNull)
      .drop("id", "role", "type")
      .as[OsmNode]

    val osmNodes = relations
      .select("relationId", "members")
      .withColumn("members", explode(col("members")))
      .withColumn("membersId", col("members").getItem("id"))
      .alias("relations")
      .join(filteredNodes, filteredNodes("nodeId") === $"relations.membersId")
      .drop("membersId", "members")
      .groupBy("relationId")
      .agg(
        collect_list(
          struct(
            $"nodeId",
            $"tags",
            $"latitude",
            $"longitude"
          )))
      .withColumnRenamed("relationId", "relationGroupId")
      .as[(Long, Seq[OsmNode])]

    val groupedRelationAndNodes = relations
      .join(osmNodes, osmNodes("relationGroupId") === col("relationId"))
      .drop("relationGroupId")
      // Spark dont likes combinations of nested custom objects
      // as[(relationId, tags, members, `type`, Seq[OsmNode])]
      .as[(Long, Map[String, String], Array[(Long, String, String)], String, Seq[OsmNode])]
      .map(t => {
        val members = t._3.map(b => OsmMember(b._1, b._2, b._3))
        //resulted tags must be added to topology
        //extractor is select, what node in relations is anchor node
        val relation = OsmRelation(t._1, t._4, t._2, members)
        (relation, t._5, getAnchorNodeId(relation))
      })

    val topologyIds = data(TopologiesTable)
      .select("topologyId", "nodes")
      .withColumn("nodes", explode(col("nodes")))
      .withColumn("membersId", col("nodes").getItem("nodeId"))
      .select("topologyId", "membersId")
      .join(groupedRelationAndNodes, groupedRelationAndNodes("_3") === col("membersId"))
      .drop("_3", "membersId")
      .withColumnRenamed("topologyId", "topologyId_")

    val mappedRules = data(TopologiesTable)
      .join(topologyIds, topologyIds("topologyId_") === $"topologyId")
      .select(
        struct($"topologyId", $"originId", $"nodes", $"tags", $"relations", $"tileIds", $"leftAdmin", $"rightAdmin") as "topology",
        $"_1" as "relation",
        $"_2" as "nodes"
      )
      .as[(Topology, OsmRelation, Seq[OsmNode])]
      .flatMap { case (topology, relation, nodes) => fillByRelation(topology, relation, nodes) }
      .groupBy("_1")
      .agg(collect_list("_2") as "list")
      .as[(String, Seq[Map[String, String]])]
      .map { case (topologyId, tags) => (topologyId, mergeTopologyTags(tags)) }

    val frame = data(TopologiesTable)
      .as("topology")
      .join(mappedRules, $"topology.topologyId" === mappedRules("_1"), "left_outer")
      .withColumn("updatedTags", joinMaps($"tags", $"_2"))
      .drop("tags", "_1", "_2")
      .withColumnRenamed("updatedTags", "tags")
      .as[Topology]

    data + (TopologiesTable -> frame)

    data
  }

  private def mergeTopologyTags(seq: Seq[Map[String, String]]) = {
    seq
      .flatMap(m => m.toList)
      .groupBy(_._1)
      .map {
        case (extractorTag, tags) =>
          val mappedTag = tags.map(_._2)
          val tag = if (mappedTag.length == 1) {
            mappedTag.head
          } else {
            //situation, when same topology have different values for same tag
            //this must be resolved by proper extractor
            extractorMap(extractorTag).resolveDuplicates(mappedTag)
          }
          (extractorTag, tag)
      }
  }

  /**
    * @return topology originId with nodes and relevant tags
    */
  private def fillByRelation(topology: Topology, relation: OsmRelation, nodes: Seq[OsmNode]) = {
    extractors.flatMap { extractor =>
        val value = extractor.decodeRelation(topology, relation, nodes)
        if (value.isEmpty) {
          None
        } else {
          val json = Json.toJson(Seq(value.get))(extractor.writes).toString()
          Some((topology.topologyId, extractor.tag -> json))
        }
      }.groupBy(_._1)
      .map(t => {
        (t._1, t._2.map(_._2).toMap)
      })
      .toSeq
  }

  private def getAnchorNodeId(relation: OsmRelation): Long = {
    extractors.flatMap { extractor =>
      extractor.getAnchorNode(relation)
    }.head
  }

  private val joinMaps = udf((map: Map[String, String], map2: Map[String, String]) => {
    if (map2 == null) {
      map
    } else {
      map ++ map2
    }
  })

}
