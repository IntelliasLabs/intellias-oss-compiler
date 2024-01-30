package com.intellias.osm.model.road

import com.intellias.osm.model.admin.FeatureAdminPlace

case class NdsRoad(topologyId: String,
                   originId: Long,
                   nodes: Array[TopologyNode],
                   tags: Map[String, String],
                   tileIds: Array[Int],
                   ndsRoadId: Int,
                   leftAdmin: Option[FeatureAdminPlace] = None,
                   rightAdmin: Option[FeatureAdminPlace] = None)