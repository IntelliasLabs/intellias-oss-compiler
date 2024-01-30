package com.intellias.osm.ndslive.name.display.line
import com.intellias.osm.model.display.{DisplayFeatureType, DisplayLine}
import com.intellias.osm.model.name.Name
import nds.name.attributes.{NameDisplayLineRangeAttributeType, NameDisplayLineRangeAttributeValue}

object NdsWaterLineNameBuilder extends NdsDisplayLineNameBuilder {
  override val attributeType: NameDisplayLineRangeAttributeType = NameDisplayLineRangeAttributeType.WATER_NAME
  override protected val lineFilter: DisplayLine => Boolean =
    displayLine => DisplayFeatureType.lookup(displayLine.featureType) == DisplayFeatureType.LineRiver

  override protected def setNameAttrValue(name: Name, attrValue: NameDisplayLineRangeAttributeValue): Unit = {
    attrValue.setWaterName(name.name)
  }
}
