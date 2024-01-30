package com.intellias.osm.ndslive.name.display.area

import com.intellias.osm.model.display.{DisplayArea, DisplayFeatureType}
import com.intellias.osm.model.name.Name
import nds.name.attributes.{NameDisplayAreaAttributeType, NameDisplayAreaAttributeValue}

object NdsNatureAreaNameBuilder extends NdsDisplayAreaNameBuilder {
  override val attributeType: NameDisplayAreaAttributeType = NameDisplayAreaAttributeType.NATURE_AREA_NAME
  override protected val areaFilter: DisplayArea => Boolean =
    displayArea => NatureAreaTypes(DisplayFeatureType.lookup(displayArea.featureType))

  override protected def setNameAttrValue(name: Name, attrValue: NameDisplayAreaAttributeValue): Unit = {
    attrValue.setNatureAreaName(name.name)
  }

  private val NatureAreaTypes: Set[DisplayFeatureType] = Set(DisplayFeatureType.AreaForestry)
}