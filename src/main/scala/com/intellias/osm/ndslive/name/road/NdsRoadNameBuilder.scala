package com.intellias.osm.ndslive.name.road

import com.intellias.mobility.geo.tools.nds.NdsTools.coordRoadShiftByte
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.NdsRoad
import com.intellias.osm.ndslive.name.{AttributeMapBuilder, NdsAdminTileBuilder}
import com.intellias.osm.ndslive.road.RoadAttributeValueBuilder
import nds.core.attributemap.ConditionList
import nds.name.attributes.NameRoadRangeAttributeType
import nds.name.instantiations.{NamePropertyList, NameRoadRangeAttribute, NameRoadRangeAttributeMap}
import nds.road.reference.types.{RoadRangeValidity, RoadValidityType}

trait NdsRoadNameBuilder extends AttributeMapBuilder with RoadAttributeValueBuilder with Serializable {
  type RoadIdAndDirection = (Int, DirectionType)
  type RoadAndAttr = (RoadIdAndDirection, NameRoadRangeAttribute, NamePropertyList, ConditionList)
  type AttrIdx = Int
  private type RoadIdx = Int

  val attributeTypeCode: NameRoadRangeAttributeType

  def buildAttributes(roads: Array[NdsRoad], adminTileBuilder: NdsAdminTileBuilder): Option[NameRoadRangeAttributeMap]

  def buildAttrMap(roadWithAttr: Seq[RoadAndAttr]): Option[NameRoadRangeAttributeMap] = {
    if (roadWithAttr.nonEmpty) {
      // group attributes by values, and get all road's Idx per group.
      val attrToSeqOfRoadIdx: Seq[((NameRoadRangeAttribute, NamePropertyList, ConditionList), Seq[RoadIdx])] = roadWithAttr.zipWithIndex.groupMap {
        case (( (roadId, direction), roadAttribute, roadPropertyList, conditionList), idx) =>
          (roadAttribute, roadPropertyList, conditionList)
      } { case (_, idx) =>
        idx
      }.toSeq

      // create references from road to attribute positions.
      val featureIdxToAttrIdx: Seq[AttrIdx] = attrToSeqOfRoadIdx.zipWithIndex
        .flatMap { case ((_, roadIdx), propIndex) =>
          roadIdx.map(pIdx => (pIdx, propIndex))
        }
        .sortBy(_._1)
        .map(_._2)

      val attrMap: NameRoadRangeAttributeMap = new NameRoadRangeAttributeMap(0.toByte)
      attrMap.setAttributeTypeCode(attributeTypeCode)

      attrMap.setFeature(roadWithAttr.size)
      attrMap.setFeatureReferences(roadWithAttr.map(ra => toRef(ra._1._1, isPositiveDirection(ra._1._2))).toArray)
      attrMap.setFeatureValidities(fillRoadValidities(roadWithAttr.size))
      attrMap.setFeatureValuePtr(featureIdxToAttrIdx.toArray)

      attrMap.setAttribute(attrToSeqOfRoadIdx.size)
      attrMap.setAttributeValues(attrToSeqOfRoadIdx.map(_._1._1).toArray)
      attrMap.setAttributeProperties(attrToSeqOfRoadIdx.map(_._1._2).toArray)
      attrMap.setAttributeConditions(attrToSeqOfRoadIdx.map(_._1._3).toArray)

      Some(attrMap)
    } else {
      None
    }
  }

  def fillRoadValidities(size: Int): Array[RoadRangeValidity] = Array.fill(size){
    new RoadRangeValidity(coordRoadShiftByte, RoadValidityType.COMPLETE, 0, null)
  }

  def isPositiveDirection(direction: DirectionType): Option[Boolean] =
    if (direction == DirectionType.Both) None
    else Option(direction == DirectionType.Forward)

}
