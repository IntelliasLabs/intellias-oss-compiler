package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.compiler.road.rules.range.{AdvisorySpeedExtractor, MaxSpeedExtractor, MinSpeedExtractor}
import com.intellias.osm.model.road.{NdsRoad, SpeedLimit}
import nds.core.attributemap.{Condition, ConditionList}
import nds.rules.attributes.{RulesRoadRangeAttributeType, RulesRoadRangeAttributeValue}
import nds.rules.instantiations.{RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}
import play.api.libs.json.{Format, Json}

//TODO: parse SpeedLimit.condition into NDS time condition
trait RoadSpeedBuilder extends RulesRangeAttributeValueBuilder with RulesVehicleTypesBuilder with Serializable {
  implicit val format: Format[SpeedLimit] = Json.format[SpeedLimit]
  def attrTypeTuple: (RulesRoadRangeAttributeType, RulesRoadRangeAttributeType)
  def extractorTag: String
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap] =
    topologies
      .flatMap(
        road =>
          road.tags
            .get(extractorTag)
            .map(maxSpeedAttrs => Json.parse(maxSpeedAttrs).as[Array[SpeedLimit]].map((road, _))))
      .flatten
      .groupMap { case (_, maxSpeed) => buildAttribute(maxSpeed) } { case (road, maxSpeed) => (road, maxSpeed.direction) }
      .map(Function.tupled(buildRoadRangeAttributeSetMap))
  private def buildAttribute(speed: SpeedLimit): RulesRoadRangeFullAttribute = {
    val (attrType, attrSetter) = if (speed.isMetric) {
      (attrTypeTuple._1, (_: RulesRoadRangeAttributeValue).setSpeedLimitKmh _)
    } else {
      (attrTypeTuple._2, (_: RulesRoadRangeAttributeValue).setSpeedLimitMph _)
    }

    val attrValue = new RulesRoadRangeAttributeValue(attrType)
    attrSetter
      .apply(attrValue)
      .apply(speed.value.toShort)

    val vehicleTypesCondition: Array[Condition] = if (speed.vehicle.isEmpty) {
      Array.empty
    } else {
      createVehicleTypeCondition(speed.vehicle.get).toArray
    }

    val array = vehicleTypesCondition ++ buildTimeCondition(speed.condition.getOrElse(""))

    new RulesRoadRangeFullAttribute(attrType, attrValue, emptyPropertyList, new ConditionList(array.length.toShort,array))
  }
}

object RoadMaxSpeedBuilder extends RoadSpeedBuilder {
  override def extractorTag: String = {
    MaxSpeedExtractor.tag
  }
  override def attrTypeTuple: (RulesRoadRangeAttributeType, RulesRoadRangeAttributeType) =
    (RulesRoadRangeAttributeType.SPEED_LIMIT_METRIC, RulesRoadRangeAttributeType.SPEED_LIMIT_IMPERIAL)
}

object RoadMinSpeedBuilder extends RoadSpeedBuilder {
  override def extractorTag: String = {
    MinSpeedExtractor.tag
  }
  override def attrTypeTuple: (RulesRoadRangeAttributeType, RulesRoadRangeAttributeType) =
    (RulesRoadRangeAttributeType.MINIMUM_SPEED_METRIC, RulesRoadRangeAttributeType.MINIMUM_SPEED_IMPERIAL)
}

object RoadAdvisorySpeedBuilder extends RoadSpeedBuilder {
  override def extractorTag: String = {
    AdvisorySpeedExtractor.tag
  }
  override def attrTypeTuple: (RulesRoadRangeAttributeType, RulesRoadRangeAttributeType) =
    (RulesRoadRangeAttributeType.ADVISORY_SPEED_LIMIT_METRIC, RulesRoadRangeAttributeType.ADVISORY_SPEED_LIMIT_IMPERIAL)
}
