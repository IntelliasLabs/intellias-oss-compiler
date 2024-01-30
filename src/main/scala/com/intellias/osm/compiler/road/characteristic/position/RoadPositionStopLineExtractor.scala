package com.intellias.osm.compiler.road.characteristic.position

import com.intellias.osm.compiler.road.highway
import com.intellias.osm.model.road.TopologyNode

object RoadPositionStopLineExtractor extends RoadPositionCharacteristicsExtractor {
  override def tag: String = "NDS:StopLine"

  override protected def isDesiredNode(node: TopologyNode): Boolean = node.tags.get(highway).contains("stop")
}
