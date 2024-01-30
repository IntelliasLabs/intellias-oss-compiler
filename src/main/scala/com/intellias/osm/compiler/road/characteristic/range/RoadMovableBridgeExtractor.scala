package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.model.road.{MovableBridgeFlag, Topology}
import play.api.libs.json.Writes

object RoadMovableBridgeExtractor extends RoadCharacteristicsExtractor[MovableBridgeFlag] with Serializable {
  override val tag: String                             = "NDS:IsMovableBridge"
  override implicit def writes: Writes[Seq[MovableBridgeFlag]] = Writes.seq

  override def decodeFromOsm(feature: Topology): Seq[MovableBridgeFlag] =
    feature.tags.get("bridge:movable") match {
      case Some(_) => Seq(MovableBridgeFlag())
      case None => Seq.empty
    }
}
