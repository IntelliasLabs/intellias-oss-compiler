package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.road.characteristic.range.RoadTypeCharsExtractor
import com.intellias.osm.compiler.road.rules.range.TrafficZoneExtractor
import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.RoadZoneType.{MotorwayZone, RuralZone, UrbanZone}
import com.intellias.osm.model.road.{NdsRoad, RoadCharacterType, RoadCharacteristics, RoadZoneType}
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import nds.rules.types.TrafficZone
import play.api.libs.json.Json

object RoadTrafficZoneBuilder extends RulesRangeAttributeValueBuilder with RulesVehicleTypesBuilder with Serializable {

  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] = {
    topologies
      .flatMap(checkTrafficZone)
      .flatten
      .groupMap { case (_, attr) => buildAttribute(attr) } { case (road, _) => (road, DirectionType.values(2)) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))
  }

  private def buildAttribute(attr: RoadZoneType): RulesRoadRangeFullAttribute = {
    val attrType: RulesRoadRangeAttributeType = RulesRoadRangeAttributeType.TRAFFIC_ZONE

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrValue.setTrafficZone(transform(attr))

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, emptyCondition)
  }

  private def transform(zone: RoadZoneType): TrafficZone = {
    zone match {
      case RuralZone | UrbanZone | MotorwayZone => TrafficZone.SPEED_LIMIT_ZONE
      case _                                    => TrafficZone.OTHER
    }
  }

  private def checkTrafficZone(road: NdsRoad) = {
    val tagOption = road.tags
      .get(TrafficZoneExtractor.tag)

    if (tagOption.isEmpty) {

      val countryCode = road.leftAdmin.getOrElse(road.rightAdmin.getOrElse(FeatureAdminPlace("", "", List.empty, 0))).isoCountryCode

      if (countryCode.equals("GER")) {
        //some roads in Germany has no speed limit, and TrafficZone.SPEED_LIMIT_ZONE cannot be assigned
        Option.empty
      } else {
        val zoneArray = findRoadZones(road.tags)
          .map((road, _))
          .toArray
        Option(zoneArray)
      }
    } else {
      tagOption
        .map(attrs => Json.parse(attrs).as[Array[RoadZoneType]].map((road, _)))
    }
  }

  private def findRoadZones(tags: Map[String, String]): Seq[RoadZoneType] = {
    tags
      .get(RoadTypeCharsExtractor.tag)
      .map {json =>
        Json.parse(json).as[Seq[RoadCharacteristics]]
          .flatMap(_.characterTypes)
          .collect{
            case RoadCharacterType.Urban => RoadZoneType.UrbanZone
            case RoadCharacterType.Motorway => RoadZoneType.MotorwayZone
          }.distinct
      }.getOrElse(Seq.empty)
  }

}
