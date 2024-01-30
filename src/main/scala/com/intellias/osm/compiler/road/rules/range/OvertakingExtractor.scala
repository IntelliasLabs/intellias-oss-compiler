package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey._
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.Topology
import play.api.libs.json.Writes

// https://wiki.openstreetmap.org/wiki/Key:overtaking
object OvertakingExtractor extends RoadRulesExtractor[DirectionType] {
  override implicit def writes: Writes[Seq[DirectionType]] = Writes.seq

  private val KeyPattern = keyPattern(
    "overtaking"
  )
  override def tag = "NDS:OvertakingProhibition"

  override def decodeFromOsm(topology: Topology): Seq[DirectionType] = {
    topology.tags.flatMap {
      case (key, rawValue) =>
        if (KeyPattern.findFirstMatchIn(key).nonEmpty) {
          //in OSM "overtaking" shows allowed cases
          //in NDS "OvertakingProhibition" shows disallowed
          rawValue match {
            case "no"       => Some(DirectionType.Both)
            case "forward"  => Some(DirectionType.Backward)
            case "backward" => Some(DirectionType.Forward)
            case _          => None
          }
        } else
          None
    }.toSeq
  }
}
