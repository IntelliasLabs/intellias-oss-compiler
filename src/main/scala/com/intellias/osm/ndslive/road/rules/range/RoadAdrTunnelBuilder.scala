package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.road.rules.range.HazmatExtractor
import com.intellias.osm.model.road.{Hazmat, NdsRoad}
import com.intellias.osm.ndslive.road.rules.range.RoadProhibitedPassageBuilder.{buildTimeCondition, createVehicleTypeConditions, emptyPropertyList}
import nds.core.attributemap.ConditionList
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import nds.rules.types.AdrTunnelCategory
import play.api.libs.json.{Format, Json}

object RoadAdrTunnelBuilder extends RulesRangeAttributeValueBuilder with Serializable {
  implicit val format: Format[Hazmat] = Json.format[Hazmat]
  private val adrList: Array[String]  = AdrTunnelCategory.values().map(_.toString)
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] =
    topologies
      .filter(road => road.tags.keys.toSeq.exists(s => s.contains("tunnel")))
      .flatMap(road =>
        road.tags
          .get(HazmatExtractor.tag)
          .map(attrs => Json.parse(attrs).as[Array[Hazmat]].map((road, _))))
      .flatten
      .filter(isHazmatInAdr)
      .groupMap { case (_, attr) => buildAttribute(attr) } { case (road, attr) => (road, attr.direction) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))

 private def isHazmatInAdr(data: (NdsRoad, Hazmat)): Boolean = {
    adrList.contains(data._2.hazmatType.toString)
  }
  private def buildAttribute(hazmat: Hazmat): RulesRoadRangeFullAttribute = {
    val attrType: RulesRoadRangeAttributeType = RulesRoadRangeAttributeType.ADR_TUNNEL_CATEGORY

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrValue.setAdrTunnelCategory(AdrTunnelCategory.toEnum(hazmat.hazmatType.toString.toUpperCase))
    val array = buildTimeCondition(hazmat.condition.getOrElse(""))

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, new ConditionList(array.length.toShort, array))
  }

}
