package com.intellias.osm.ndslive.name.display.area

import com.intellias.osm.model.display.{DisplayArea, DisplayFeatureType}
import com.intellias.osm.model.name.Name
import nds.name.attributes.{NameDisplayAreaAttributeType, NameDisplayAreaAttributeValue}

object NdsWaterAreaNameBuilder extends NdsDisplayAreaNameBuilder {
  override val attributeType: NameDisplayAreaAttributeType = NameDisplayAreaAttributeType.WATER_NAME
  override protected val areaFilter: DisplayArea => Boolean =
    displayArea => WaterAreaTypes(DisplayFeatureType.lookup(displayArea.featureType))

  override protected def setNameAttrValue(name: Name, attrValue: NameDisplayAreaAttributeValue): Unit = {
    attrValue.setWaterName(name.name)
  }

  private val WaterAreaTypes: Set[DisplayFeatureType] = Set(DisplayFeatureType.AreaSeaOcean)
}
