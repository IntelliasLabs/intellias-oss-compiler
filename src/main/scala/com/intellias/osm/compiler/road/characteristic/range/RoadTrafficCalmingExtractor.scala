package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.{keyPattern, required}
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.road.characteristic.values.OsmTrafficCalmingType
import com.intellias.osm.compiler.road.rules.values.OsmDirection
import com.intellias.osm.model.common.FeatureRange
import com.intellias.osm.model.road.{Topology, TrafficCalming}
import play.api.libs.json.Writes

object RoadTrafficCalmingExtractor extends RoadCharacteristicsExtractor[TrafficCalming] with Serializable {
  val tag = "NDS:TrafficCalming"
  override implicit def writes: Writes[Seq[TrafficCalming]] = Writes.seq

  private val key = "traffic_calming"
  private val KeyPattern = keyPattern(key, required(OsmDirection))

  override def decodeFromOsm(topology: Topology): Seq[TrafficCalming] = {
    topology.tags.flatMap {
      case (key, value) => KeyPattern.findFirstMatchIn(key).map(rm =>(OsmDirection(rm), OsmTrafficCalmingType(value)))
    }.flatMap {
      case (osmDirection, calmingType) => Seq(TrafficCalming(calmingType.calmingType, osmDirection.direction, Seq(FeatureRange.Complete)))
      case _ => Seq.empty
    }.toSeq
  }

}
