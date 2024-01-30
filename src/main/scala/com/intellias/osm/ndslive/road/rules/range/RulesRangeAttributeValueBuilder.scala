package com.intellias.osm.ndslive.road.rules.range

import com.intellias.mobility.geo.tools.nds.NdsTools.coordRoadShiftByte
import com.intellias.osm.compiler.condition.OsmOpeningHoursToCondition
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.NdsRoad
import com.intellias.osm.ndslive.common.NdsTimeConditionBuilder
import com.intellias.osm.ndslive.road.RoadAttributeValueBuilder
import nds.core.attributemap.{Condition, ConditionList}
import nds.road.reference.types._
import nds.rules.instantiations.{RulesProperty, RulesPropertyList, RulesRoadRangeAttributeSet, RulesRoadRangeAttributeSetMap, RulesRoadRangeFullAttribute}

trait RulesRangeAttributeValueBuilder extends RoadAttributeValueBuilder {
  private val COMPLETE_RANGE = new RoadRangeValidity(coordRoadShiftByte, RoadValidityType.COMPLETE, 0, null)

  val emptyCondition: ConditionList = new ConditionList(0.toShort, Array.empty[Condition])
  val emptyPropertyList = new RulesPropertyList(0.toShort, Array.empty[RulesProperty])

  def buildAttributes(topologies: Array[NdsRoad]): Iterable[RulesRoadRangeAttributeSetMap]

  def buildRoadRangeAttributeSetMap(attribute: RulesRoadRangeFullAttribute, refs: Array[(NdsRoad, DirectionType)]): RulesRoadRangeAttributeSetMap = {
    val roadRangeAttributeSet = new RulesRoadRangeAttributeSet(1, Array(attribute))

    val roadReferences = refs.map { case (road, direction) =>
      toRef(
        road.ndsRoadId,
        if (direction == DirectionType.Both) None
        else Option(direction == DirectionType.Forward)
      )
    }

    val attributeSetMap = new RulesRoadRangeAttributeSetMap(coordRoadShiftByte)
    attributeSetMap.setAttributeSet(roadRangeAttributeSet)
    attributeSetMap.setFeature(roadReferences.length)
    attributeSetMap.setReferences(roadReferences)
    attributeSetMap.setValidities(refs.map(_ => COMPLETE_RANGE))

    attributeSetMap
  }

  def buildTimeCondition(time: String): Array[Condition] = {
    OsmOpeningHoursToCondition
      .buildCondition(time)
      .getOrElse(Iterable.empty)
      .toSeq
      .map {
        NdsTimeConditionBuilder.toCondition
      }.toArray
  }
}
