package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.keyPattern
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.road.characteristic.values.OsmRoadClassPattern
import com.intellias.osm.model.road.{RoadClass, Topology}
import play.api.libs.json.Writes

object FunctionalClassExtractor extends RoadCharacteristicsExtractor[RoadClass] with Serializable {
  override val tag: String                             = "NDS:RoadFunctionalClass"
  override implicit def writes: Writes[Seq[RoadClass]] = Writes.seq

  private val KeyPattern = keyPattern("highway")

  override def decodeFromOsm(feature: Topology): Seq[RoadClass] =
    feature.tags.flatMap {
      case (key, value) => KeyPattern.findFirstMatchIn(key).map(_ => OsmRoadClassPattern(value))
    }.map(classPattern =>
      RoadClass(classPattern.classNum)
    ).toSeq

}
