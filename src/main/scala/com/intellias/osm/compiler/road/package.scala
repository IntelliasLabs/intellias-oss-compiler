package com.intellias.osm.compiler

import com.intellias.osm.{AdminPlaceConfig, CommonConfig, NdsLiveConfig, OsmConfig, RoadConfig, StorageConfig}
import com.intellias.osm.common.{SourceType, StorageService}
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.language.{LanguageConfig, LanguageService}
import com.intellias.osm.model.common.wrapper.MultiPolygonWrapper
import com.intellias.osm.model.road.{RoadTile, Topology}

package object road {
  val highway = "highway"

  trait RoadEnvironment extends StorageService with LanguageService with AdminPlaceService with Serializable

  case class RoadWorkflowConfig(common: CommonConfig,
                                ndsStorage: StorageConfig,
                                osm: OsmConfig,
                                nds: NdsLiveConfig,
                                adminPlace: AdminPlaceConfig,
                                langConfig: LanguageConfig,
                                road: RoadConfig)

  case object NdsRoadTileTable extends SourceType[RoadTile]


  case class TopologyWithArea(topology: Topology, areas: List[FeatureArea])

  case class FeatureArea(areaId: Long, tags: Map[String, String], derivedFrom: String, areaGeometry: MultiPolygonWrapper)
}
