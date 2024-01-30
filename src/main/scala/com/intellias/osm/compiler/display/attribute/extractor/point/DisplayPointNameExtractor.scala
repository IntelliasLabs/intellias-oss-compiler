package com.intellias.osm.compiler.display.attribute.extractor.point

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.compiler.display.DisplayEnvironment
import com.intellias.osm.model.display.DisplayPoint
import com.intellias.osm.model.name.Name
import play.api.libs.json.Writes

class DisplayPointNameExtractor(val env: DisplayEnvironment) extends DisplayPointAttributeExtractor[Name] with OsmNameExtractor {
  override implicit def writes: Writes[Seq[Name]] = Writes.seq

  override def decodeFromOsm(point: DisplayPoint): Seq[Name] = decodeNames(point.tags, point.adminPlace)
}
