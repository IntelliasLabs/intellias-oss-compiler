package com.intellias.osm.compiler.poi.attributes

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.compiler.poi.PoiEnvironment
import com.intellias.osm.model.name.Name
import com.intellias.osm.model.poi.POI
import play.api.libs.json.Writes


case class PoiNameExtractor(env: PoiEnvironment) extends PoiAttributeExtractor[Name] with OsmNameExtractor {
  override implicit def writes: Writes[Seq[Name]] = Writes.seq

  def decodeFromOsm(poi: POI): Seq[Name] = decodeNames(poi.tags, poi.adminPlace)
}
