package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.keyPattern
import com.intellias.osm.compiler.attributes.keys.{OsmReferenceType, ValuesSeparator}
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.schema.model.road.RoadNumber
import com.intellias.osm.model.road.Topology
import play.api.libs.json.Writes

/*
  OSM wiki: https://wiki.openstreetmap.org/wiki/Key%3Aref
 */
object RoadNumberExtractor extends RoadCharacteristicsExtractor[RoadNumber] with Serializable {
  override val tag: String                              = "NDS:RoadNumber"
  override implicit def writes: Writes[Seq[RoadNumber]] = Writes.seq

  private val KeyPattern = keyPattern(OsmReferenceType)

  override def decodeFromOsm(feature: Topology): Seq[RoadNumber] = {
    feature.tags.flatMap {
      case (key, value) =>
        KeyPattern.findFirstMatchIn(key).map(refType => OsmReferenceType(refType) -> value)
    }.flatMap {
      case (referenceType, numbers) => parseValue(numbers).map(number => RoadNumber(number, referenceType.refType))
    }.toSeq
  }

  private def parseValue(value: String): Seq[String] = value.split(ValuesSeparator)

}
