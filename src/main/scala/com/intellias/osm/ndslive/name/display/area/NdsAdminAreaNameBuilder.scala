package com.intellias.osm.ndslive.name.display.area

import com.intellias.osm.model.display.{DisplayArea, DisplayFeatureType}
import com.intellias.osm.model.name.Name
import nds.name.attributes.{NameDisplayAreaAttributeType, NameDisplayAreaAttributeValue}

object NdsAdminAreaNameBuilder extends NdsDisplayAreaNameBuilder {
  override val attributeType: NameDisplayAreaAttributeType = NameDisplayAreaAttributeType.ADMINISTRATIVE_AREA_NAME
  override protected val areaFilter: DisplayArea => Boolean =
    displayArea => DisplayFeatureType.lookup(displayArea.featureType) == DisplayFeatureType.AreaUrban

  override protected def setNameAttrValue(name: Name, attrValue: NameDisplayAreaAttributeValue): Unit = {
    attrValue.setAdministrativeAreaName(name.name)
  }
}
