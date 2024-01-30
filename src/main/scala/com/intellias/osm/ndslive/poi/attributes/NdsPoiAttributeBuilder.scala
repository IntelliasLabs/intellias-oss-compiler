package com.intellias.osm.ndslive.poi.attributes

import com.intellias.osm.model.poi.POI
import nds.core.attributemap.{Condition, ConditionList, Validity}
import nds.poi.attributes.PoiAttributeType
import nds.poi.instantiations.{PoiAttribute, PoiAttributeMap, PoiProperty, PoiPropertyList}
import nds.poi.properties.{PoiPropertyType, PoiPropertyValue, PropertyType, PropertyValue}

trait NdsPoiAttributeBuilder extends Serializable {
  type PoiId = Int
  type PoiIdAndAttr = (PoiId, PoiAttribute, PoiPropertyList, ConditionList)
  type AttrIdx = Int
  val emptyCondition: ConditionList = new ConditionList(0.toShort, Array.empty[Condition])
  val emptyProperty: PoiPropertyList = new PoiPropertyList(0.toShort, Array.empty[PoiProperty])
  private type PoiIdx = Int

  val attributeTypeCode: PoiAttributeType


  def buildAttributes(pois: Array[POI]): Option[PoiAttributeMap]

  def buildAttrMap(poiWithAttr: Seq[PoiIdAndAttr]): Option[PoiAttributeMap] = {
    if(poiWithAttr.nonEmpty) {
      //group attributes by values, and get all poi's Idx per group.
      val attrToSeqOfPoiIdx: Seq[((PoiAttribute, PoiPropertyList, ConditionList), Seq[PoiIdx])] = poiWithAttr.zipWithIndex
        .groupMap(v => (v._1._2, v._1._3, v._1._4)) { case (_, idx) => idx }.toSeq

      // create references from poi to attribute positions.
      val featureIdxToAttrIdx: Seq[AttrIdx] = attrToSeqOfPoiIdx.zipWithIndex.flatMap {
        case ((_, poiIdx), propIndex) => poiIdx.map(pIdx => (pIdx, propIndex))
      }.sortBy(_._1).map(_._2)

      val attrMap: PoiAttributeMap = new PoiAttributeMap(0.toByte)
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

  def fillValidities(size: Int): Array[Validity] = Array.fill(size)(new Validity(0.toByte))

  def buildLanguagePropertyList(langId: Seq[Short]): PoiPropertyList =
    new PoiPropertyList(langId.size.toShort, langId.map(buildLanguageProperty).toArray)

  def buildLanguageProperty(langId: Short): PoiProperty = {
    val propValue = new PropertyValue(PropertyType.LANGUAGE_CODE)
    propValue.setLanguageCode(langId)

    val poiPropVal = new PoiPropertyValue(langPropTypeCode)
    poiPropVal.setValue(propValue)

    new PoiProperty(langPropTypeCode, poiPropVal)
  }

  val langPropTypeCode: PoiPropertyType = {
    val propTypeCode = new PoiPropertyType()
    propTypeCode.setType(PropertyType.LANGUAGE_CODE)
    propTypeCode
  }
}
