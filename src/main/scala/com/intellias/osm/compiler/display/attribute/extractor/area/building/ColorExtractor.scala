package com.intellias.osm.compiler.display.attribute.extractor.area.building

import com.intellias.osm.compiler.display.attribute.extractor.area.DisplayAreaAttributeExtractor
import com.intellias.osm.compiler.display.attribute.value.area.Color
import com.intellias.osm.model.display.DisplayArea
import play.api.libs.json.Writes

trait ColorExtractor extends DisplayAreaAttributeExtractor[Color] {
  override implicit def writes: Writes[Seq[Color]] = Writes.seq

  override def decodeFromOsm(building: DisplayArea): Seq[Color] = {
    building.tags.get("building:colour")
      .flatMap(Color.lookup)
      .toSeq
  }
}

object RoofColorExtractor extends ColorExtractor {
  override def tag: String = "NDS:Building:RoofColor"
}

object WallsColorExtractor extends ColorExtractor {
  override def tag: String = "NDS:Building:WallsColor"
}