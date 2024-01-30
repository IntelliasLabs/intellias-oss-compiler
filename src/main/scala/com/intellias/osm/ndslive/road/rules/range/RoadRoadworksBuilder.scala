package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.road.rules.range.{ConstructionExtractor, HazardExtractor}
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{Construction, Hazard, NdsRoad}
import nds.core.types.Flag
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import play.api.libs.json.Json

object RoadRoadworksBuilder extends RulesRangeAttributeValueBuilder with RulesVehicleTypesBuilder with Serializable {

  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] = {
    topologies
      .flatMap(getRoadworks)
      .flatten
      .groupMap { case (_, _) => buildAttribute() } { case (road, direction) => (road, direction) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))
  }

  private def getRoadworks(road: NdsRoad): Option[Array[(NdsRoad, DirectionType)]] = {
    val hazards = road.tags
      .get(HazardExtractor.tag)
      .map(attrs => Json.parse(attrs).as[Array[Hazard]].map((road, _)))
      .getOrElse(Array.empty)
      .map { case (road, hazard) => (road, hazard.direction) }

    val constructions = road.tags
      .get(ConstructionExtractor.tag)
      .map(attrs => Json.parse(attrs).as[Array[Construction]].map((road, _)))
      .getOrElse(Array.empty)
      .map { case (road, _) => (road, DirectionType.Both) }

    Option(hazards ++ constructions)
  }

  private def buildAttribute(): RulesRoadRangeFullAttribute = {
    val attrType: RulesRoadRangeAttributeType = RulesRoadRangeAttributeType.ROADWORKS

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrValue.setRoadworks(new Flag())

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, emptyCondition)
  }

}
