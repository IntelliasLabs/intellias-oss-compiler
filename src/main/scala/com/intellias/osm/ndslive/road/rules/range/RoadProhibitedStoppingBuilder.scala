package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.road.rules.range.{MarginalKeysExtractor, ParkingExtractor, TrafficSignExtractor}
import com.intellias.osm.model.common.{DirectionType, Side}
import com.intellias.osm.model.road.{MarginalKeyType, NdsRoad, ParkingType, TrafficSignType}
import nds.core.attributemap.ConditionList
import nds.core.types.Flag
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import play.api.libs.json.Json

object RoadProhibitedStoppingBuilder extends RulesRangeAttributeValueBuilder with RulesVehicleTypesBuilder with Serializable {

  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] = {
    topologies
      .flatMap(isRoadProhibitedStopping)
      .flatten
      .groupMap { case (_, _, condition) => buildAttribute(condition) } { case (road, direction, _) => (road, direction) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))
  }

  private def isRoadProhibitedStopping(road: NdsRoad): Option[Seq[(NdsRoad, DirectionType, Option[String])]] = {
    val signProhibition = road.tags
      .get(TrafficSignExtractor.tag)
      .map(attrs => Json.parse(attrs).as[Array[TrafficSignType]])
      .getOrElse(Array.empty)
      .filter(sign => sign.direction == DirectionType.Both && sign.signType.equals("no_stopping"))
      .map(sign => (road, sign.direction, sign.condition))
      .toSeq

    val parkingProhibition = road.tags
      .get(ParkingExtractor.tag)
      .map(attrs => Json.parse(attrs).as[Array[ParkingType]])
      .getOrElse(Array.empty)
      .filter(parking => parking.key.getOrElse("").equals("restriction") && parking.value.equals("no_stopping"))
      .map(parkingType => {
        val direction = parkingType.side match {
          case Side.Both  => DirectionType.Both
          case Side.Right => DirectionType.Forward
          case Side.Left  => DirectionType.Backward
        }
        (road, direction.asInstanceOf[DirectionType], parkingType.condition)
      })
      .toSeq

    val marginalKeyProhibition = road.tags
      .get(MarginalKeysExtractor.tag)
      .map(attrs => Json.parse(attrs).as[Array[MarginalKeyType]])
      .getOrElse(Array.empty)
      .filter(key => {
        key.key match {
          case "stopping"            => key.value.equals("no")
          case "traffic_restriction" => key.value.equals("no_stopping")
          case _                     => false
        }
      })
      .map(_ => (road, DirectionType.values(2), Option.empty))
      .toSeq

    Option(signProhibition ++ parkingProhibition ++ marginalKeyProhibition)
  }

  private def buildAttribute(condition: Option[String]): RulesRoadRangeFullAttribute = {
    val attrType: RulesRoadRangeAttributeType = RulesRoadRangeAttributeType.PROHIBITED_STOPPING

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrValue.setProhibitedStopping(new Flag())

    val array = buildTimeCondition(condition.getOrElse(""))

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, new ConditionList(array.length.toShort, array))
  }

}
