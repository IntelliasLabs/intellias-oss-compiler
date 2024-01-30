package com.intellias.osm.compiler.road.characteristic.position

import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.model.road.TopologyNode

object RoadPositionTollStationExtractor extends RoadPositionCharacteristicsExtractor {
  override def tag: String = "NDS:TollStation"

  override def isDesiredNode(node: TopologyNode): Boolean = node.tagValue("barrier", "toll_booth")
}
