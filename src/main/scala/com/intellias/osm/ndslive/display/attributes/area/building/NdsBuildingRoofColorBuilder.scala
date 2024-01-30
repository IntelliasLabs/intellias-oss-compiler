package com.intellias.osm.ndslive.display.attributes.area.building

import com.intellias.osm.compiler.display.attribute.extractor.area.building.RoofColorExtractor
import com.intellias.osm.compiler.display.attribute.value.area.Color
import com.intellias.osm.ndslive.display.attributes.area.NdsDisplayAreaAttributeBuilder
import nds.display.details.attributes.{DisplayAreaAttributeType, DisplayAreaAttributeValue}
import play.api.libs.json.Json

object NdsBuildingRoofColorBuilder extends NdsDisplayAreaAttributeBuilder[Color] with BuildingFootprintFilter with ColorConverter {
  override val attributeType: DisplayAreaAttributeType = DisplayAreaAttributeType.ROOF_COLOR
  override val sourceTag: String = RoofColorExtractor.tag

  override protected def toNdsAttribute(sourceRoofColor: Color): Option[DisplayAreaAttributeValue] = {
    toNdsColor(sourceRoofColor).map { ndsRoofColor =>
      val ndsAttrValue = new DisplayAreaAttributeValue(attributeType)
      ndsAttrValue.setRoofColor(ndsRoofColor)
      ndsAttrValue
    }
  }

  override protected def parseFromJson(json: String): Seq[Color] = Json.parse(json).as[Seq[Color]]
}
