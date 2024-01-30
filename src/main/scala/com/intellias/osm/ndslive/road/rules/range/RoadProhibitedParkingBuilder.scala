package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.road.rules.range.ParkingExtractor
import com.intellias.osm.model.common.{DirectionType, Side}
import com.intellias.osm.model.road.{NdsRoad, ParkingType}
import nds.core.attributemap.ConditionList
import nds.core.types.Flag
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import play.api.libs.json.Json

object RoadProhibitedParkingBuilder extends RulesRangeAttributeValueBuilder with RulesVehicleTypesBuilder with Serializable {

  private val forbiddenParking = Set("no", "no_parking", "no_stopping", "no_standing")
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] = {
    topologies
      .flatMap(
        road =>
          road.tags
            .get(ParkingExtractor.tag)
            .map(attrs => { Json.parse(attrs).as[Array[ParkingType]].map((road, _)) }))
      .flatten
      .flatMap {
        case (road, parking) =>
          val maybeType = isParkingProhibited(parking)
          if (maybeType.nonEmpty) {
            Some(road, parking, maybeType.get)
          } else {
            None
          }
      }
      .groupMap { case (_, parkingType, _) => buildAttribute(parkingType) } { case (road, _, direction) => (road, direction) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))
  }

  private def isParkingProhibited(parking: ParkingType): Option[DirectionType] = {
    //according to attribute definition
    //"Parking is not allowed on the road that is represented by the feature the attribute is assigned to."

    if (parking.key.isEmpty &&
        parking.value.equals("no") || parking.key.getOrElse("").equals("restriction")
        && forbiddenParking.contains(parking.value)) {

      val direction = parking.side match {
        case Side.Both  => DirectionType.Both
        case Side.Right => DirectionType.Forward
        case Side.Left  => DirectionType.Backward
      }
      Some(direction)
    } else {
      None
    }
  }

  private def buildAttribute(parkingType: ParkingType): RulesRoadRangeFullAttribute = {
    val attrType: RulesRoadRangeAttributeType = RulesRoadRangeAttributeType.PROHIBITED_PARKING

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrValue.setProhibitedParking(new Flag())

    val array = buildTimeCondition(parkingType.condition.getOrElse(""))

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, new ConditionList(array.length.toShort, array))
  }

}
