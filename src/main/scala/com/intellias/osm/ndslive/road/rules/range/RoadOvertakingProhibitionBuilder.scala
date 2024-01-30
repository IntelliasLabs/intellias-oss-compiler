package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.road.rules.range.OvertakingExtractor
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.NdsRoad
import nds.core.types.Flag
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import play.api.libs.json.Json

object RoadOvertakingProhibitionBuilder extends RulesRangeAttributeValueBuilder with Serializable {

  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] =
    topologies
      .flatMap(
        road =>
          road.tags
            .get(OvertakingExtractor.tag)
            .map(attrs => Json.parse(attrs).as[Array[DirectionType]].map((road, _))))
      .flatten
      .groupMap { case (_, _) => buildAttribute() } { case (road, attr) => (road, attr) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))

  private def buildAttribute(): RulesRoadRangeFullAttribute = {
    val attrType: RulesRoadRangeAttributeType = RulesRoadRangeAttributeType.OVERTAKING_PROHIBITION

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrValue.setOvertakingProhibition(new Flag())

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, emptyCondition)
  }

}
