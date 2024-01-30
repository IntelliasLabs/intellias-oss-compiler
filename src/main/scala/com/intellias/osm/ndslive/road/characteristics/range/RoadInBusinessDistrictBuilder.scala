package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.area.RoadAreaBusinessDistrictExtractor
import com.intellias.osm.compiler.road.characteristic.range.RoadMovableBridgeExtractor
import com.intellias.osm.compiler.road.common.range.RoadRange
import com.intellias.osm.model.common.{DirectionType, FeatureRange}
import com.intellias.osm.model.road.{InBusinessDistrict, MovableBridgeFlag, NdsRoad}
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.core.attributemap.{Condition, ConditionList}
import nds.core.types.Flag
import play.api.libs.json.Json
import  com.intellias.osm.compiler.road.common.range.RoadRange._

object RoadInBusinessDistrictBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    val refs: Array[(NdsRoad, DirectionType, RoadRange)] = topologies.flatMap { ndsRoad =>
      ndsRoad.tags
        .get(RoadAreaBusinessDistrictExtractor.tag)
        .map( json => Json.parse(json).as[Seq[InBusinessDistrict]].map(dist => ndsRoad -> dist))
    }.flatten
      .flatMap { case (road, district) =>
        district.ranges.map(range => (road, DirectionType.Both, range.toRoadRange))
      }

    Iterable(buildMap(refs))
  }

  private def buildMap(refs: Array[(NdsRoad, DirectionType, RoadRange)]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.IN_BUSINESS_DISTRICT)
    attributeValue.setHasSidewalk(new Flag())

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.IN_BUSINESS_DISTRICT,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]),
      new ConditionList(0.toShort, Array.empty[Condition])
    )

    buildRoadRangeAttributeSetMap(refs, roadTypeFullAttribute)
  }
}
