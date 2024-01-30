package com.intellias.osm.model.road

case class IntersectNode(nodeId: Long,
                         nodeTileId: Int,
                         longitude: Double,
                         latitude: Double,
                         zLevel: Int,
                         isVirtual: Boolean)