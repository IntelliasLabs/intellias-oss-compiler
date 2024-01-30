package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.RoadMovableBridgeExtractor
import com.intellias.osm.compiler.road.common.range.RoadRangeComplete
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{MovableBridgeFlag, NdsRoad}
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.core.attributemap.{Condition, ConditionList}
import nds.core.types.Flag
import play.api.libs.json.Json

object RoadMovableBridgeBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    val movableBridgeRoads: Array[NdsRoad] = topologies.flatMap { ndsRoad =>
      ndsRoad.tags
        .get(RoadMovableBridgeExtractor.tag)
        .map( json => Json.parse(json).as[Seq[MovableBridgeFlag]].map(_ => ndsRoad))
    }.flatten

    Seq(buildMap(movableBridgeRoads))
  }

  private def buildMap(refs: Array[NdsRoad]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.MOVABLE_BRIDGE)
    attributeValue.setHasSidewalk(new Flag())

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.MOVABLE_BRIDGE,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]),
      new ConditionList(0.toShort, Array.empty[Condition])
    )

    buildRoadRangeAttributeSetMap(refs.map((_, DirectionType.Both, RoadRangeComplete())), roadTypeFullAttribute)
  }
}
