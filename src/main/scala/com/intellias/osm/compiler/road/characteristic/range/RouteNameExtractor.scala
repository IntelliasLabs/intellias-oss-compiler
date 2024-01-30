package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.model.name.Name
import com.intellias.osm.model.road.Topology
import play.api.libs.json.Writes

//https://wiki.openstreetmap.org/wiki/Key%3Aref
case class RouteNameExtractor(env: LanguageService) extends RoadCharacteristicsExtractor[Name] with OsmNameExtractor with Serializable {
  override val tag: String                              = "NDS:RouteNumber"
  override implicit def writes: Writes[Seq[Name]] = Writes.seq

  override def decodeFromOsm(feature: Topology): Seq[Name] = decodeNames(feature.tags, feature.leftAdmin)

}
