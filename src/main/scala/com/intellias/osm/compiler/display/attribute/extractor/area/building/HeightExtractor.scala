package com.intellias.osm.compiler.display.attribute.extractor.area.building

import com.intellias.osm.compiler.display.attribute.extractor.area.DisplayAreaAttributeExtractor
import com.intellias.osm.compiler.display.attribute.value.area.building.Height
import com.intellias.osm.model.display.DisplayArea
import play.api.libs.json.Writes

object HeightExtractor extends DisplayAreaAttributeExtractor[Height] {
  override def tag: String = "NDS:Building:Height"
  private val heightRegex = "^(\\d+\\.?\\d*)(.*m)?$".r

  override implicit def writes: Writes[Seq[Height]] = Writes.seq

  private def parseHeightValue(rawValue: String): Option[Float] = {
    if (heightRegex.matches(rawValue)) {
      val heightRegex(heightValue, _) = rawValue
      Some(heightValue.toFloat)
    } else {
      None
    }
  }

  override def decodeFromOsm(building: DisplayArea): Seq[Height] = {
    lazy val maybeRoofHeight = building.tags.get("roof:height").flatMap(parseHeightValue)
    building.tags.get("height")
      .flatMap(parseHeightValue)
      .map(heightValue => Height(heightValue + maybeRoofHeight.getOrElse(0f)))
      .toSeq
  }
}
