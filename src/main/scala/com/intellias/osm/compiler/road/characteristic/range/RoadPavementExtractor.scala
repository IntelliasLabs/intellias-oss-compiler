package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.keyPattern
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.road.characteristic.values.OsmRoadPavementPattern
import com.intellias.osm.model.road.{PavementType, Topology}
import play.api.libs.json.Writes

object RoadPavementExtractor extends RoadCharacteristicsExtractor[PavementType] with Serializable {
  val tag = "NDS:PavementType"
  private val KeyPattern = keyPattern("surface")

  override implicit def writes: Writes[Seq[PavementType]] = Writes.seq
  override def decodeFromOsm(feature: Topology): Seq[PavementType] =
    feature.tags.flatMap {
      case (key, value) => KeyPattern.findFirstMatchIn(key).map(_ => OsmRoadPavementPattern(value))
    }.map(_.pavementType).toSeq
}
