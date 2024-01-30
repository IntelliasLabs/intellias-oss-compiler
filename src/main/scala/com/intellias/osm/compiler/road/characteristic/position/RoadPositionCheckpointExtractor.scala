package com.intellias.osm.compiler.road.characteristic.position

import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.model.road.TopologyNode

object RoadPositionCheckpointExtractor extends RoadPositionCharacteristicsExtractor {
  override val tag: String = "NDS:Checkpoint"

  override protected def isDesiredNode(node: TopologyNode): Boolean =
    node.oneOf("barrier" -> "border_control", "military" -> "checkpoint", "police" -> "checkpoint")

}
