package com.intellias.osm.ndslive.name.poi

import com.intellias.osm.model.poi.POI
import com.intellias.osm.ndslive.name.{AttributeMapBuilder, NdsAdminTileBuilder}
import nds.core.attributemap.ConditionList
import nds.name.attributes.NamePoiAttributeType
import nds.name.instantiations.{NamePoiAttribute, NamePoiAttributeMap, NamePropertyList}

trait NdsPoiNameBuilder extends AttributeMapBuilder with Serializable {
  type PoiId = Int
  type PoiIdAndAttr = (PoiId, NamePoiAttribute, NamePropertyList, ConditionList)
  type AttrIdx = Int
  private type PoiIdx = Int

  val attributeTypeCode: NamePoiAttributeType

  def buildAttributes(pois: Array[POI], adminTileBuilder: NdsAdminTileBuilder): Option[NamePoiAttributeMap]

  def buildAttrMap(poiWithAttr: Seq[PoiIdAndAttr]): Option[NamePoiAttributeMap] = {
    if (poiWithAttr.nonEmpty) {
      // group attributes by values, and get all poi's Idx per group.
      val attrToSeqOfPoiIdx: Seq[((NamePoiAttribute, NamePropertyList, ConditionList), Seq[PoiIdx])] = poiWithAttr.zipWithIndex.groupMap {
        case ((poiId, poiAttribute, poiPropertyList, conditionList), idx) =>
          (poiAttribute, poiPropertyList, conditionList)
      } { case (_, idx) =>
        idx
      }.toSeq

      // create references from poi to attribute positions.
      val featureIdxToAttrIdx: Seq[AttrIdx] = attrToSeqOfPoiIdx.zipWithIndex
        .flatMap { case ((_, poiIdx), propIndex) =>
          poiIdx.map(pIdx => (pIdx, propIndex))
        }
        .sortBy(_._1)
        .map(_._2)

      val attrMap: NamePoiAttributeMap = new NamePoiAttributeMap(0.toByte)
      attrMap.setAttributeTypeCode(attributeTypeCode)

      attrMap.setFeature(poiWithAttr.size)
      attrMap.setFeatureReferences(poiWithAttr.map(_._1).toArray)
      attrMap.setFeatureValidities(fillValidities(poiWithAttr.size))
      attrMap.setFeatureValuePtr(featureIdxToAttrIdx.toArray)

      attrMap.setAttribute(attrToSeqOfPoiIdx.size)
      attrMap.setAttributeValues(attrToSeqOfPoiIdx.map(_._1._1).toArray)
      attrMap.setAttributeProperties(attrToSeqOfPoiIdx.map(_._1._2).toArray)
      attrMap.setAttributeConditions(attrToSeqOfPoiIdx.map(_._1._3).toArray)

      Some(attrMap)
    } else {
      None
    }
  }
}
