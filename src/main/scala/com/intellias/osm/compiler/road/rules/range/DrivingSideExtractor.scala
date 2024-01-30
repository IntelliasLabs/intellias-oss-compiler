package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey._
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.Topology
import play.api.libs.json.Writes

// https://wiki.openstreetmap.org/wiki/Key:driving_side
object DrivingSideExtractor extends RoadRulesExtractor[DirectionType] {
  override implicit def writes: Writes[Seq[DirectionType]] = Writes.seq

  private val KeyPattern = keyPattern(
    "driving_side"
  )
  override def tag = "NDS:DrivingSide"

  override def decodeFromOsm(topology: Topology): Seq[DirectionType] = {
    topology.tags.flatMap {
      case (key, _) =>
        if (KeyPattern.findFirstMatchIn(key).nonEmpty) {
          Some(DirectionType.Both)
        } else
          None
    }.toSeq
  }
}
