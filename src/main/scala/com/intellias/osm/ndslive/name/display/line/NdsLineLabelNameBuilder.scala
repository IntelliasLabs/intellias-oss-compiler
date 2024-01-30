package com.intellias.osm.ndslive.name.display.line
import com.intellias.osm.model.display.{DisplayFeatureType, DisplayLine}
import com.intellias.osm.model.name.Name
import nds.name.attributes.{NameDisplayLineRangeAttributeType, NameDisplayLineRangeAttributeValue}

object NdsLineLabelNameBuilder extends NdsDisplayLineNameBuilder {
  override val attributeType: NameDisplayLineRangeAttributeType = NameDisplayLineRangeAttributeType.LINE_LABEL_NAME
  override protected val lineFilter: DisplayLine => Boolean =
    displayLine => LineTypes(DisplayFeatureType.lookup(displayLine.featureType))

  override protected def setNameAttrValue(name: Name, attrValue: NameDisplayLineRangeAttributeValue): Unit = {
    attrValue.setLabelName(name.name)
  }

  // this Builder is aimed to handle a fallback scenario: when certain type of display line
  // is not handled by other builders it should be picked up by this one
  private val LineTypes: Set[DisplayFeatureType] = Set(DisplayFeatureType.LineRailway)
}
