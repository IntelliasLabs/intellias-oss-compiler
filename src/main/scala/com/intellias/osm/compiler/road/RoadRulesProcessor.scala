package com.intellias.osm.compiler.road

import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.attributes.AttributeExtractorLike
import com.intellias.osm.compiler.road.rules.range._
import com.intellias.osm.compiler.topology.TopologiesTable
import com.intellias.osm.model.road.Topology
import com.intellias.osm.tools.JsonMapperProvider
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import play.api.libs.json.Json

object RoadRulesProcessor extends Processor with JsonMapperProvider with StrictLogging with Serializable {
  private val extractors: Seq[AttributeExtractorLike[Topology]] = Seq(MaxSpeedExtractor, MinSpeedExtractor, AdvisorySpeedExtractor,
    OvertakingExtractor, AccessExtractor, DrivingSideExtractor, TrafficZoneExtractor, HazmatExtractor, SeasonalExtractor,
    ParkingExtractor, TrafficSignExtractor, MarginalKeysExtractor, HazardExtractor, ConstructionExtractor)

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val frame = data(TopologiesTable)
      .map(topology => topology.copy(tags = topology.tags ++ populateRoadRules(topology)))

    data + (TopologiesTable -> frame)
  }

  private def populateRoadRules(topology: Topology): Seq[(String, String)] = {
    extractors.flatMap { extractor =>
      val rules = extractor.decodeFromOsm(topology)
      if (rules.nonEmpty) {
        val json = Json.toJson(rules)(extractor.writes).toString()
        Some(extractor.tag -> json)
      } else None
    }
  }
}
