package com.intellias.osm.compiler.road.characteristic.area

import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.compiler.road.FeatureArea
import com.intellias.osm.compiler.road.characteristic.range.RoadTypeCharsExtractor
import com.intellias.osm.compiler.road.characteristic.range.RoadTypeCharsExtractor.{getExistChars, mergeCharacteristicsByRange}
import com.intellias.osm.model.common.FeatureRange
import com.intellias.osm.model.road.{RoadCharacterType, RoadCharacteristics, Topology}
import play.api.libs.json.Writes

object RoadAreaUrbanExtractor extends RoadAreaCharacteristicsExtractor[RoadCharacteristics]{
  override val tag: String                             = RoadTypeCharsExtractor.tag
  override implicit def writes: Writes[Seq[RoadCharacteristics]] = Writes.seq

  override def decodeFromOsm(feature: Topology, areas: Seq[FeatureArea]): Seq[RoadCharacteristics] = {
    val existedCharacteristics: Seq[(FeatureRange, Seq[RoadCharacterType])] = getExistChars(feature)
    val newCharcteristics = RangeBuilder.findRanges(feature, areas.filter(isUrbanArea))
      .map(range => range -> Seq(RoadCharacterType.Urban))

    mergeCharacteristicsByRange(existedCharacteristics, newCharcteristics)
  }

  private def isUrbanArea(areas: FeatureArea): Boolean = {
    areas.tags.oneOf("landuse", Set("residential")) ||
      areas.tags.oneOf("place", Set("village", "borough", "suburb", "quarter", "neighbourhood", "city_block", "town", "hamlet", "allotments"))
  }


}
