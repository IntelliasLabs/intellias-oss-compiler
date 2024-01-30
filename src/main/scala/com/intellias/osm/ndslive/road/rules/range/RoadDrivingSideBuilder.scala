package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.road.rules.range.DrivingSideExtractor
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.NdsRoad
import nds.core.types.Flag
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import play.api.libs.json.Json

object RoadDrivingSideBuilder extends RulesRangeAttributeValueBuilder with Serializable {

  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] =
    topologies
      .flatMap(
        road =>
          road.tags
            .get(DrivingSideExtractor.tag)
            .map(attrs => Json.parse(attrs).as[Array[DirectionType]].map((road, _))))
      .flatten
      .groupMap { case (_, _) => buildAttribute() } { case (road, attr) => (road, attr) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))

  private def buildAttribute(): RulesRoadRangeFullAttribute = {
    val attrType: RulesRoadRangeAttributeType = RulesRoadRangeAttributeType.NON_DEFAULT_DRIVING_SIDE

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrValue.setNonDefaultDrivingSide(new Flag())

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, emptyCondition)
  }

}
