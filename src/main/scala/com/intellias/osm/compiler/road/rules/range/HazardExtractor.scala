package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.{keyPattern, optional}
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.compiler.road.rules.values.OsmDirection
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{Hazard, Topology}
import play.api.libs.json.Writes

//https://wiki.openstreetmap.org/wiki/Key:hazard
object HazardExtractor extends RoadRulesExtractor[Hazard] {

  override implicit def writes: Writes[Seq[Hazard]] = Writes.seq
  override def tag                                  = "NDS:Hazard"

  private val KeyPattern = keyPattern("hazard", optional(OsmDirection))

  override def decodeFromOsm(topology: Topology): Seq[Hazard] = {
    topology.tags.flatMap {
      case (key, value) =>
        KeyPattern
          .findFirstMatchIn(key)
          .map(rm => (OsmDirection(rm), value))
          .map { case (direction, value) => Hazard(value, direction.direction) }
    }.toSeq
  }

}
