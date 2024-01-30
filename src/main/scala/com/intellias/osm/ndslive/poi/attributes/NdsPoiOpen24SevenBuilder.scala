package com.intellias.osm.ndslive.poi.attributes

import com.intellias.osm.compiler.poi.attributes.PoiOpen24SevenExtractor
import com.intellias.osm.model.poi.POI
import nds.core.types.Flag
import nds.poi.attributes.PoiAttributeType.OPEN_24_HRS
import nds.poi.attributes.{PoiAttributeType, PoiAttributeValue}
import nds.poi.instantiations.{PoiAttribute, PoiAttributeMap}

object NdsPoiOpen24SevenBuilder extends NdsPoiAttributeBuilder {
  override val attributeTypeCode: PoiAttributeType = OPEN_24_HRS

  override def buildAttributes(pois: Array[POI]): Option[PoiAttributeMap] = {
    val poisWithAttr = pois.filter(p => p.tags.contains(PoiOpen24SevenExtractor.tag))

    if (poisWithAttr.nonEmpty) {
      val attrMap: PoiAttributeMap = new PoiAttributeMap(0.toByte)
      attrMap.setAttributeTypeCode(attributeTypeCode)

      attrMap.setFeature(poisWithAttr.length) // size of feature references and pointers
      attrMap.setFeatureReferences(poisWithAttr.map(_.ndsId))
      attrMap.setFeatureValidities(fillValidities(poisWithAttr.length))
      attrMap.setFeatureValuePtr(poisWithAttr.map(_ => 0))
      attrMap.setAttribute(1) //this Attribute doesn't have any value just flag.

      val attr    = new PoiAttribute(attributeTypeCode)
      val attrVal = new PoiAttributeValue(attributeTypeCode)
      attrVal.setOpen24Hrs(new Flag())
      attr.setAttributeValue(attrVal)

      attrMap.setAttributeValues(Array(attr))

      val attrProp = emptyProperty
      attrMap.setAttributeProperties(Array(attrProp))

      val cond = emptyCondition
      attrMap.setAttributeConditions(Array(cond))

      Some(attrMap)
    } else
      None
  }
}
