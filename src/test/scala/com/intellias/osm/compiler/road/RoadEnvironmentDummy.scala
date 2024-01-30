package com.intellias.osm.compiler.road

import com.intellias.osm.common.StorageService
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.DummyEnvironment
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.road.{Relation, Topology, TopologyNode}

trait RoadEnvironmentDummy extends DummyEnvironment {

  val poiEnv: RoadEnvironment = new RoadEnvironment {
    override val adminPlaceService: AdminPlaceService.Service = dummyEnv.adminPlaceService
    override val storageServices: Seq[StorageService.Service] = dummyEnv.storageServices
    override val langService: LanguageService.Service =  dummyEnv.langService
  }

  def creatDummyTopology(
      tags: Map[String, String],
      topologyId: String = "osm-node-12",
      originId: Long = 123,
      nodes: Array[TopologyNode] = Array(),
      relations: List[Relation] = List(),
      tileIds: Array[Int] = Array(545366801),
      leftAdmin: Option[FeatureAdminPlace] = dummyAdminPlace,
      rightAdmin: Option[FeatureAdminPlace] = dummyAdminPlace
  ): Topology = {
    Topology(topologyId, originId, nodes, tags, relations, tileIds, leftAdmin, rightAdmin)
  }
}
