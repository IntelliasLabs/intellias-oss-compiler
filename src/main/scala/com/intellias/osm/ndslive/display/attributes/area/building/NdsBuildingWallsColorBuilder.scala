package com.intellias.osm.ndslive.display.attributes.area.building

import com.intellias.osm.compiler.display.attribute.extractor.area.building.WallsColorExtractor
import com.intellias.osm.compiler.display.attribute.value.area.Color
import com.intellias.osm.ndslive.display.attributes.area.NdsDisplayAreaAttributeBuilder
import nds.display.details.attributes.{DisplayAreaAttributeType, DisplayAreaAttributeValue}
import play.api.libs.json.Json

object NdsBuildingWallsColorBuilder extends NdsDisplayAreaAttributeBuilder[Color] with BuildingFootprintFilter with ColorConverter {
  override val attributeType: DisplayAreaAttributeType = DisplayAreaAttributeType.WALL_COLOR
  override val sourceTag: String = WallsColorExtractor.tag

  override def toNdsAttribute(sourceWallsColor: Color): Option[DisplayAreaAttributeValue] = {
    toNdsColor(sourceWallsColor).map { ndsWallsColor =>
      val ndsAttrValue = new DisplayAreaAttributeValue(attributeType)
      ndsAttrValue.setWallColor(ndsWallsColor)
      ndsAttrValue
    }
  }

  override protected def parseFromJson(json: String): Seq[Color] = Json.parse(json).as[Seq[Color]]
}
