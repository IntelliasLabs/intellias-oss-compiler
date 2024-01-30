package com.intellias.osm.ndslive.display.attributes.area.building

import com.intellias.osm.compiler.display.attribute.extractor.area.building.FloorCountExtractor
import com.intellias.osm.compiler.display.attribute.value.area.building.FloorCount
import com.intellias.osm.ndslive.display.attributes.area.NdsDisplayAreaAttributeBuilder
import nds.display.details.attributes.{DisplayAreaAttributeType, DisplayAreaAttributeValue}
import play.api.libs.json.Json

object NdsBuildingFloorsBuilder extends NdsDisplayAreaAttributeBuilder[FloorCount] with BuildingFootprintFilter {
  override val attributeType: DisplayAreaAttributeType = DisplayAreaAttributeType.BUILDING_FLOOR_COUNT
  override val sourceTag: String = FloorCountExtractor.tag

  override def toNdsAttribute(sourceFloorCount: FloorCount): Option[DisplayAreaAttributeValue] = {
    val attrValue = new DisplayAreaAttributeValue(attributeType)
    attrValue.setBuildingFloorCount(Math.ceil(sourceFloorCount.value).toShort)
    Some(attrValue)
  }

  override protected def parseFromJson(json: String): Seq[FloorCount] = Json.parse(json).as[Seq[FloorCount]]
}
