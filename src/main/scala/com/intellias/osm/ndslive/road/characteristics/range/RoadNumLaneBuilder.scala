package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.RoadNumLanesExtractor
import com.intellias.osm.compiler.road.common.range.RoadRangeComplete
import com.intellias.osm.compiler.schema.model.road.RoadNumLanes
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.NdsRoad
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.core.attributemap.{Condition, ConditionList}
import play.api.libs.json.Json

object RoadNumLaneBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    topologies.flatMap { ndsRoad =>
      ndsRoad.tags
        .get(RoadNumLanesExtractor.tag)
        .map(json => Json.parse(json).as[Seq[RoadNumLanes]].map(ndsRoad -> _.lanesNum))
    }.flatten
      .groupBy(_._2)
      .map {
        case (numLanes, topologyAndValue) => buildMap(numLanes, topologyAndValue.map(_._1))
      }
  }

  private def buildMap(numLanes: Int, refs: Array[NdsRoad]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.NUM_LANES)
    attributeValue.setNumLanes(numLanes.toShort)

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.NUM_LANES,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]),
      new ConditionList(0.toShort, Array.empty[Condition])
    )

    buildRoadRangeAttributeSetMap(refs.map((_, DirectionType.Both, RoadRangeComplete())), roadTypeFullAttribute)
  }
}
