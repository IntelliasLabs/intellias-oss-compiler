package com.intellias.osm.compiler.admin.attributes

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.model.admin.{AdminPlace, FeatureAdminPlace}
import com.intellias.osm.model.name.Name
import play.api.libs.json.Writes

case class AdminNameExtractor(env: LanguageService) extends AdminAttributeExtractor[Name] with OsmNameExtractor {
  override implicit def writes: Writes[Seq[Name]] = Writes.seq

  def decodeFromOsm(adminPlace: AdminPlace): Seq[Name] = decodeNames(
    adminPlace.tags,
    Some(FeatureAdminPlace(adminPlace.adminPlaceId, adminPlace.isoCountryCode, adminPlace.isoSubCountryCode, adminPlace.adminLevel))
  )
}
