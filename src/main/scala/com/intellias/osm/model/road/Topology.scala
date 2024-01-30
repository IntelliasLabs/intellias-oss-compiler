package com.intellias.osm.model.road

import com.intellias.osm.model.admin.FeatureAdminPlace

case class Topology(topologyId: String,
                    originId: Long,
                    nodes: Array[TopologyNode],
                    tags: Map[String, String],
                    relations: List[Relation],
                    tileIds: Array[Int],
                    leftAdmin: Option[FeatureAdminPlace] = None,
                    rightAdmin: Option[FeatureAdminPlace] = None)