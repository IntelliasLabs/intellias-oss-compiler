package com.intellias.osm.ndslive.display.attributes.area.building

import com.intellias.osm.compiler.display.attribute.extractor.area.building.HeightExtractor
import com.intellias.osm.compiler.display.attribute.value.area.building.Height
import com.intellias.osm.ndslive.display.attributes.area.NdsDisplayAreaAttributeBuilder
import nds.display.details.attributes.{DisplayAreaAttributeType, DisplayAreaAttributeValue}
import play.api.libs.json.Json

object NdsBuildingHeightBuilder extends NdsDisplayAreaAttributeBuilder[Height] with BuildingFootprintFilter {
  override val attributeType: DisplayAreaAttributeType = DisplayAreaAttributeType.BUILDING_HEIGHT
  override val sourceTag: String = HeightExtractor.tag

  override protected def toNdsAttribute(sourceHeight: Height): Option[DisplayAreaAttributeValue] = {
    val ndsAttrValue = new DisplayAreaAttributeValue(attributeType)
    ndsAttrValue.setBuildingHeight(Math.round(sourceHeight.value * 100)) // in centimeters
    Some(ndsAttrValue)
  }

  override protected def parseFromJson(json: String): Seq[Height] = Json.parse(json).as[Seq[Height]]
}
