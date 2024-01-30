package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.RoadStartOrDestinationRoadOnlyExtractor
import com.intellias.osm.compiler.road.common.range.RoadRangeComplete
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{NdsRoad, StartOrDestinationRoadFlag}
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.core.attributemap.{Condition, ConditionList}
import nds.core.types.Flag
import play.api.libs.json.Json

object RoadStartOrDestinationRoadOnlyBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    val startOrDestRoads: Array[NdsRoad] = topologies.flatMap { ndsRoad =>
      ndsRoad.tags
        .get(RoadStartOrDestinationRoadOnlyExtractor.tag)
        .map( json => Json.parse(json).as[Seq[StartOrDestinationRoadFlag]].map(_ => ndsRoad))
    }.flatten

    Seq(buildMap(startOrDestRoads))
  }

  private def buildMap(refs: Array[NdsRoad]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.START_OR_DESTINATION_ROAD_ONLY)
    attributeValue.setHasSidewalk(new Flag())

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.START_OR_DESTINATION_ROAD_ONLY,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]),
      new ConditionList(0.toShort, Array.empty[Condition])
    )

    buildRoadRangeAttributeSetMap(refs.map((_, DirectionType.Both, RoadRangeComplete())), roadTypeFullAttribute)
  }
}
