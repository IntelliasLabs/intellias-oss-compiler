package com.intellias.osm.ndslive.name.display.area

import com.intellias.osm.model.display.{DisplayArea, DisplayFeatureType}
import com.intellias.osm.model.name.Name
import nds.name.attributes.{NameDisplayAreaAttributeType, NameDisplayAreaAttributeValue}

object NdsAreaLabelNameBuilder extends NdsDisplayAreaNameBuilder {
  override val attributeType: NameDisplayAreaAttributeType = NameDisplayAreaAttributeType.AREA_LABEL_NAME
  override protected val areaFilter: DisplayArea => Boolean =
    displayArea => AreaTypes(DisplayFeatureType.lookup(displayArea.featureType))

  override protected def setNameAttrValue(name: Name, attrValue: NameDisplayAreaAttributeValue): Unit = {
    attrValue.setAreaName(name.name)
  }

  // this Builder is aimed to handle a fallback scenario: when certain type of display area
  // is not handled by other builders it should be picked up by this one
  private val AreaTypes: Set[DisplayFeatureType] = Set(DisplayFeatureType.AreaIsland)
}
