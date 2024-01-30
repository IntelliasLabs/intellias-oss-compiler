package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.condition.OsmOpeningHoursToCondition
import com.intellias.osm.compiler.road.rules.range.AccessExtractor
import com.intellias.osm.model.road.{DeniedAccess, NdsRoad}
import com.intellias.osm.ndslive.common.NdsTimeConditionBuilder
import nds.core.attributemap.ConditionList
import nds.core.types.Flag
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import play.api.libs.json.Json

object RoadProhibitedPassageBuilder extends RulesRangeAttributeValueBuilder with RulesVehicleTypesBuilder with Serializable {

  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] =
    topologies
      .flatMap(
        road =>
          road.tags
            .get(AccessExtractor.tag)
            .map(attrs => Json.parse(attrs).as[Array[DeniedAccess]].map((road, _))))
      .flatten
      .groupMap { case (_, attr) => buildAttribute(attr) } { case (road, attr) => (road, attr.direction) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))

  private def buildAttribute(attr: DeniedAccess): RulesRoadRangeFullAttribute = {
    val attrType: RulesRoadRangeAttributeType = RulesRoadRangeAttributeType.PROHIBITED_PASSAGE

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrValue.setProhibitedPassage(new Flag())

    val array = (createVehicleTypeConditions(attr.vehicle) ++ buildTimeCondition(attr.condition.getOrElse(""))).toArray

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, new ConditionList(array.length.toShort, array))
  }

}
