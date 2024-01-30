package com.intellias.osm.compiler.display.attribute.extractor.area

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.compiler.display.DisplayEnvironment
import com.intellias.osm.model.display.DisplayArea
import com.intellias.osm.model.name.Name
import play.api.libs.json.Writes

class DisplayAreaNameExtractor(val env: DisplayEnvironment) extends DisplayAreaAttributeExtractor[Name] with OsmNameExtractor{
  override implicit def writes: Writes[Seq[Name]] = Writes.seq

  override def decodeFromOsm(area: DisplayArea): Seq[Name] = {
    area.adminPlaces.flatMap(adminPlace => decodeNames(area.tags, Option(adminPlace)))
  }
}
