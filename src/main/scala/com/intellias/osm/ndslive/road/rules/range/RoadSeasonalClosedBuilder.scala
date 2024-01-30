package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.road.rules.range.SeasonalExtractor
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{NdsRoad, Season}
import nds.core.types.Flag
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import play.api.libs.json.Json

object RoadSeasonalClosedBuilder extends RulesRangeAttributeValueBuilder with RulesVehicleTypesBuilder with Serializable {

  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] = {
    topologies
      .flatMap(
        road =>
          road.tags
            .get(SeasonalExtractor.tag)
            .map(attrs => terrs(road, attrs)))
      .flatten
      .groupMap { case (_, _) => buildAttribute() } { case (road, _) => (road, DirectionType.values(2)) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))
  }

  private def terrs(road: NdsRoad, attrs: String) = {
    val tuples = Json.parse(attrs).as[Array[Season]].map((road, _))
    tuples
  }

  private def buildAttribute(): RulesRoadRangeFullAttribute = {
    val attrType: RulesRoadRangeAttributeType = RulesRoadRangeAttributeType.SEASONAL_CLOSED

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrValue.setSeasonalClosed(new Flag())

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, emptyCondition)
  }

}
