package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey._
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.model.road.{RoadZoneType, Topology}
import play.api.libs.json.Writes

// https://wiki.openstreetmap.org/wiki/Key:zone:traffic

object TrafficZoneExtractor extends RoadRulesExtractor[RoadZoneType] {

  override implicit def writes: Writes[Seq[RoadZoneType]] = Writes.seq
  override def tag                                        = "NDS:TrafficZone"

  private val KeyPattern = keyPattern("zone\\:traffic")

  override def decodeFromOsm(topology: Topology): Seq[RoadZoneType] = {
    topology.tags.flatMap {
      case (key, rawValue) =>
        if (KeyPattern
              .findFirstMatchIn(key)
              .isEmpty) {
          None
        } else {
          val traffic = rawValue.split(":")(1)
          Option(RoadZoneType.toRoadZone(traffic))
        }
    }.toSeq
  }

}
