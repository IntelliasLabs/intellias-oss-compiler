package com.intellias.osm.compiler.display.attribute.extractor.area

import com.intellias.osm.compiler.display.attribute.value.area.GlobalId
import com.intellias.osm.model.display.DisplayArea
import play.api.libs.json.Writes

object GlobalIdExtractor extends DisplayAreaAttributeExtractor[GlobalId] {
  override def tag: String = "NDS:Area:GlobalId"

  override implicit def writes: Writes[Seq[GlobalId]] = Writes.seq

  override def decodeFromOsm(area: DisplayArea): Seq[GlobalId] = {
    Seq(GlobalId(area.originalId))
  }
}
