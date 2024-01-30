package com.intellias.osm.ndslive.name.display.area
import com.intellias.osm.model.display.{DisplayArea, DisplayFeatureType}
import com.intellias.osm.model.name.Name
import nds.name.attributes.{NameDisplayAreaAttributeType, NameDisplayAreaAttributeValue}

object NdsBuildingNameBuilder extends NdsDisplayAreaNameBuilder {
  override val attributeType: NameDisplayAreaAttributeType = NameDisplayAreaAttributeType.BUILDING_NAME
  override protected val areaFilter: DisplayArea => Boolean = displayArea =>
    DisplayFeatureType.lookup(displayArea.featureType) == DisplayFeatureType.BuildingFootprint
  override protected def setNameAttrValue(name: Name, attrValue: NameDisplayAreaAttributeValue): Unit = {
    attrValue.setBuildingName(name.name)
  }
}
