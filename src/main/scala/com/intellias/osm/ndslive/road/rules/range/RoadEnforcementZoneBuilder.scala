package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.road.rules.range.EnforcementExtractor
import com.intellias.osm.model.road.{EnforcementType, EnforcementZone, NdsRoad}
import com.intellias.osm.ndslive.road.rules.range.RoadAdrTunnelBuilder.buildRoadRangeAttributeSetMap
import com.intellias.osm.ndslive.road.rules.range.RoadProhibitedParkingBuilder.buildRoadRangeAttributeSetMap
import nds.core.types.TrafficEnforcementZoneType
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import nds.rules.types.TrafficEnforcementZone
import play.api.libs.json.{Format, Json}

object RoadEnforcementZoneBuilder extends RulesRangeAttributeValueBuilder with RulesVehicleTypesBuilder with Serializable {
  implicit val format: Format[EnforcementZone] = Json.format[EnforcementZone]
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] =
    topologies
      .flatMap(
        road =>
          road.tags
            .get(EnforcementExtractor.tag)
            .map(attrs => terrs(road, attrs)))
      .flatten
      .groupMap { case (_, attr) => buildAttribute(attr) } { case (road, attr) => (road, attr.direction) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))

  private def terrs(road: NdsRoad, attrs: String) = {
    val tuples = Json.parse(attrs).as[Array[EnforcementZone]].map((road, _))
    tuples
  }

  private def buildAttribute(attr: EnforcementZone): RulesRoadRangeFullAttribute = {
    val attrType: RulesRoadRangeAttributeType = RulesRoadRangeAttributeType.TRAFFIC_ENFORCEMENT_ZONE

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrValue.setTrafficEnforcementZone(transform(attr))

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, emptyCondition)
  }

  private def transform(zone: EnforcementZone): TrafficEnforcementZone = {
    val zoneType = zone.enforcementType match {
      case EnforcementType.SpeedEnforcementZone  => TrafficEnforcementZoneType.SPEED_ENFORCEMENT_ZONE
      case EnforcementType.RiskZone              => TrafficEnforcementZoneType.RISK_ZONE
      case EnforcementType.DangerZone            => TrafficEnforcementZoneType.DANGER_ZONE
      case EnforcementType.AverageSpeedZone      => TrafficEnforcementZoneType.AVERAGE_SPEED_ZONE
      case EnforcementType.AccidentBlackspotZone => TrafficEnforcementZoneType.ACCIDENT_BLACKSPOT_ZONE
    }

    new TrafficEnforcementZone(zoneType, zone.lengthInMeters)
  }

}
