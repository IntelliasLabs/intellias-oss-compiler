package com.intellias.osm.model.road

case class TopologyNode(nodeIdx: Int,
                        nodeId: Long,
                        longitude: Double,
                        latitude: Double,
                        isIntersectNode: Boolean,
                        isFirstOrLastNode: Boolean,
                        zLevel: Int,
                        nodeTileId: Int,
                        tags: Map[String, String],
                        isVirtual: Boolean = false)