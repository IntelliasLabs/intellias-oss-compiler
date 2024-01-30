package com.intellias.osm.ndslive.name.display.point
import com.intellias.osm.model.display.{DisplayFeatureType, DisplayPoint}
import com.intellias.osm.model.name.Name
import nds.name.attributes.{NameDisplayPointAttributeType, NameDisplayPointAttributeValue}

object NdsAdminCenterNameBuilder extends NdsDisplayPointNameBuilder {
  override val attributeType: NameDisplayPointAttributeType = NameDisplayPointAttributeType.ADMINISTRATIVE_AREA_CENTER_NAME
  override protected val pointFilter: DisplayPoint => Boolean =
    displayPoint => DisplayFeatureType.lookup(displayPoint.featureType) == DisplayFeatureType.PointCityCenter

  override protected def setNameAttrValue(name: Name, attrValue: NameDisplayPointAttributeValue): Unit = {
    attrValue.setAdministrativeAreaCenterName(name.name)
  }
}
