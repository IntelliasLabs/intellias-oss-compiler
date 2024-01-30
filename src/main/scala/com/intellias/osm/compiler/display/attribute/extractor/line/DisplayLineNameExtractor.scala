package com.intellias.osm.compiler.display.attribute.extractor.line

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.compiler.display.DisplayEnvironment
import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.common.Side
import com.intellias.osm.model.display.DisplayLine
import com.intellias.osm.model.name.Name
import play.api.libs.json.Writes

class DisplayLineNameExtractor(val env: DisplayEnvironment) extends DisplayLineAttributeExtractor[Name] with OsmNameExtractor {
  override implicit def writes: Writes[Seq[Name]] = Writes.seq

  override def decodeFromOsm(displayLine: DisplayLine): Seq[Name] = {
    if (displayLine.leftAdminPlaces.toSet.equals(displayLine.rightAdminPlaces.toSet)) {
      extractNames(displayLine.tags, displayLine.leftAdminPlaces)
    } else {
      val leftNames = extractNames(displayLine.tags, displayLine.leftAdminPlaces, Side.Left)
      val rightNames = extractNames(displayLine.tags, displayLine.rightAdminPlaces, Side.Right)
      leftNames ++ rightNames
    }
  }

  private def extractNames(tags: Map[String, String], adminPlaces: List[FeatureAdminPlace]): Seq[Name] = {
    adminPlaces.flatMap(adminPlace => decodeNames(tags, Option(adminPlace)))
  }

  private def extractNames(tags: Map[String, String], adminPlaces: List[FeatureAdminPlace], adminsSide: Side): Seq[Name] = {
    extractNames(tags, adminPlaces).map(name => name.copy(side = adminsSide))
  }
}
