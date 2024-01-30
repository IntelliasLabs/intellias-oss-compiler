package com.intellias.osm.ndslive.display.attributes.area
import com.intellias.osm.compiler.display.attribute.extractor.area.GlobalIdExtractor
import com.intellias.osm.compiler.display.attribute.value.area.GlobalId
import nds.display.details.attributes.{DisplayAreaAttributeType, DisplayAreaAttributeValue}
import play.api.libs.json.Json

object NdsAreaGlobalIdBuilder extends NdsDisplayAreaAttributeBuilder[GlobalId] {
  override val attributeType: DisplayAreaAttributeType = DisplayAreaAttributeType.GLOBAL_SOURCE_ID
  override val sourceTag: String = GlobalIdExtractor.tag

  override def toNdsAttribute(sourceGlobalId: GlobalId): Option[DisplayAreaAttributeValue] = {
    val attributeValue = new DisplayAreaAttributeValue(attributeType)
    attributeValue.setGlobalSourceId(sourceGlobalId.id)
    Some(attributeValue)
  }

  override protected def parseFromJson(json: String): Seq[GlobalId] = Json.parse(json).as[Seq[GlobalId]]
}
