package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.RoadCarpoolExtractor
import com.intellias.osm.compiler.road.common.range.RoadRangeComplete
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{Carpool, CarpoolType, NdsRoad}
import com.intellias.osm.ndslive.common.NdsTimeConditionBuilder
import nds.characteristics.attributes.CharacsRoadRangeAttributeType.{COMPLETE_CARPOOL_ROAD, PARTIAL_CARPOOL_ROAD}
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.core.attributemap.{Condition, ConditionList}
import nds.core.conditions.{ConditionTypeCode, ConditionValue, Occupancy, OccupancyCount}
import nds.core.types.Flag
import play.api.libs.json.Json

object RoadCarpoolBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    val roadsWithConditions: Array[(NdsRoad, CharacsRoadRangeAttributeType, ConditionList)] = topologies.flatMap { ndsRoad =>
      ndsRoad.tags
        .get(RoadCarpoolExtractor.tag)
        .map { json =>
          Json.parse(json).as[Seq[Carpool]].map { carpool =>
            val timeConditions     = carpool.timeConditions.map(NdsTimeConditionBuilder.toCondition)
            val occupancyCondition = toOccupancyCondition(carpool.occupancy)
            (ndsRoad, toNdsCarpoolType(carpool.carpoolType), NdsTimeConditionBuilder.toConditionList(timeConditions.toArray :+ occupancyCondition))
          }
        }
    }.flatten

    roadsWithConditions.groupMap { case (_, poolType, condList) => (poolType, condList) }(_._1).map {
      case ((poolType, condList), refs) =>
        buildMap(poolType, condList, refs)
    }
  }

  private def buildMap(poolType: CharacsRoadRangeAttributeType,
                       conditionList: ConditionList,
                       refs: Array[NdsRoad]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(poolType)
    poolType match {
      case COMPLETE_CARPOOL_ROAD => attributeValue.setCompleteCarpoolRoad(new Flag())
      case PARTIAL_CARPOOL_ROAD  => attributeValue.setPartialCarpoolRoad(new Flag())
      case _                     => throw new IllegalStateException("This scenario should never happen")
    }

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      poolType,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]),
      conditionList
    )

    buildRoadRangeAttributeSetMap(refs.map((_, DirectionType.Both, RoadRangeComplete())), roadTypeFullAttribute)
  }

  def toNdsCarpoolType(carpoolType: CarpoolType): CharacsRoadRangeAttributeType = carpoolType match {
    case CarpoolType.Partial  => CharacsRoadRangeAttributeType.PARTIAL_CARPOOL_ROAD
    case CarpoolType.Complete => CharacsRoadRangeAttributeType.COMPLETE_CARPOOL_ROAD
  }

  def toOccupancyCondition(n: Int): Condition = {
    val conValue = new ConditionValue(ConditionTypeCode.OCCUPANCY)
    conValue.setOccupancy(new Occupancy(OccupancyCount.toEnum((n - 2).toByte), true))
    new Condition(ConditionTypeCode.OCCUPANCY, conValue)
  }
}
