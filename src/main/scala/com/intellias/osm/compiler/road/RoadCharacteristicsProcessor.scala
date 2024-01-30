package com.intellias.osm.compiler.road

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.attributes.AttributeExtractorLike
import com.intellias.osm.compiler.road.characteristic.position.{RoadPositionCheckpointExtractor, RoadPositionPorterExtractor, RoadPositionStopLineExtractor}
import com.intellias.osm.compiler.road.characteristic.range._
import com.intellias.osm.compiler.topology.TopologiesTable
import com.intellias.osm.model.road.Topology
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import play.api.libs.json.Json

case class RoadCharacteristicsProcessor(commonConf: CommonConfig, env: RoadEnvironment) extends Processor with StrictLogging with Serializable {
  private val extractors: Seq[AttributeExtractorLike[Topology]] = Seq(
    FunctionalClassExtractor,
    RoadPavementExtractor,
    RoadNumberExtractor,
    RoadPhysicalWidthMetricExtractor,
    RoadHasPedestrianCrossingExtractor,
    RoadTrafficCalmingExtractor,
    RoadNumLanesExtractor,
    RoadHasSidewalkExtractor,
    RoadMovableBridgeExtractor,
    RoadStartOrDestinationRoadOnlyExtractor,
    RoadSharedRoadSurfaceWithPedestriansExtractor,
    RoadRailwayCrossingExtractor,
    RoadPositionStopLineExtractor,
    RoadPositionPorterExtractor,
    RoadPositionCheckpointExtractor,
    RoadTypeFormExtractor,
    RoadTypeCharsExtractor,
    RoadHasStreetLightsExtractor,
    RoadCarpoolExtractor,
    RoadNameExtractor(env)
  )

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val ds = data(TopologiesTable)
      .map(populateRoadCharacteristics)

    data + (TopologiesTable -> ds)
  }

  private def populateRoadCharacteristics(feature: Topology): Topology = {
    val attributes = extractors.flatMap { extractor =>
      val attributes = extractor.decodeFromOsm(feature)
      if (attributes.nonEmpty) {
        val json = Json.toJson(attributes)(extractor.writes).toString()
        Some(extractor.tag -> json)
      } else None
    }

    feature.copy(tags = feature.tags ++ attributes)
  }
}
