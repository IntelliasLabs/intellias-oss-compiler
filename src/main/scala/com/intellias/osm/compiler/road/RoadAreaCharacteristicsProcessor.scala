package com.intellias.osm.compiler.road

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.compiler.datasource.osm.{OsmRelation, OsmWay}
import com.intellias.osm.compiler.road.RoadAreaCharacteristicsProcessor._
import com.intellias.osm.compiler.road.characteristic.area.{AttributeAreaExtractorLike, RoadAreaBusinessDistrictExtractor, RoadAreaUrbanExtractor}
import com.intellias.osm.compiler.topology.TopologiesTable
import com.intellias.osm.model.road.Topology
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.{Dataset, SparkSession}
import play.api.libs.json.Json

case class RoadAreaCharacteristicsProcessor(commonConf: CommonConfig) extends Processor with StrictLogging {
  private val extractors: Seq[AttributeAreaExtractorLike[Topology]] = Seq(RoadAreaBusinessDistrictExtractor, RoadAreaUrbanExtractor)

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    val oldDF = data(TopologiesTable)
    val newData = data + (TopologiesTable -> enrichTags(data))
    oldDF.unpersist()
    newData
  }

  private def enrichTags(data: SharedProcessorData)(implicit spark: SparkSession): Dataset[Topology] = {
    import spark.implicits._

    val topologyWithAreas = new RoadAreaCollector(data, commonConf).collect(isDesiredArea, isDesiredArea)

    topologyWithAreas.map(tp => populateRoadCharacteristics(tp.topology, tp.areas))
  }

  private def populateRoadCharacteristics(topology: Topology, areas: List[FeatureArea]): Topology = {
    val attributes = extractors.flatMap { extractor =>
      val attributes = extractor.decodeFromOsm(topology, areas)
      if (attributes.nonEmpty) {
        val json = Json.toJson(attributes)(extractor.writes).toString()
        Some(extractor.tag -> json)
      } else None
    }

    topology.copy(tags = topology.tags ++ attributes)
  }
}

object RoadAreaCharacteristicsProcessor {
  def isDesiredArea(way: OsmWay): Boolean = isArea(way.nodeIds) && hasDesiredTags(way.tags)
  def isDesiredArea(way: OsmRelation): Boolean = hasDesiredTags(way.tags)

  private def hasDesiredTags(tags: Map[String, String]): Boolean = {
    tags.oneOf("landuse", Set("commercial", "retail", "industrial", "residential")) ||
      tags.oneOf("place", Set("village", "borough", "suburb", "quarter", "neighbourhood", "city_block", "town", "hamlet", "allotments"))
  }

  private def isArea(nodeIds: Array[Long]) = nodeIds.head == nodeIds.last

}

