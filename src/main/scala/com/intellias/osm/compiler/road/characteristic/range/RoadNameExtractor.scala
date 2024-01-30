package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.model.common.Side
import com.intellias.osm.model.name.Name
import com.intellias.osm.model.road.Topology
import play.api.libs.json.Writes


case class RoadNameExtractor(env: LanguageService) extends RoadCharacteristicsExtractor[Name] with OsmNameExtractor {
  override implicit def writes: Writes[Seq[Name]] = Writes.seq

  override def decodeFromOsm(feature: Topology): Seq[Name] = {
    if(feature.leftAdmin == feature.rightAdmin) {
      decodeNames(feature.tags, feature.leftAdmin)
    } else {
      val leftNames = decodeNames(feature.tags, feature.leftAdmin)
      val rightNames = decodeNames(feature.tags, feature.rightAdmin)

      leftNames.map(n => n.copy(side = Side.Left)) ++ rightNames.map(n => n.copy(side = Side.Right))
    }
  }
}
