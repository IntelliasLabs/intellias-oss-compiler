package com.intellias.osm.compiler.road.characteristic.position

import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.model.road.TopologyNode

object RoadPositionPorterExtractor extends RoadPositionCharacteristicsExtractor {
  override def tag: String = "NDS:Porter"

  override protected def isDesiredNode(node: TopologyNode): Boolean =
    node.oneOf(
      "barrier",
      Set(
        "swing_gate",
        "lift_gate",
        "kissing_gate",
        "chain",
        "sliding_gate",
        "turnstile",
        "rope",
        "full-height_turnstile",
        "bump_gate",
        "spikes",
        "coupure",
        "sliding_beam"
      )
    )

}
