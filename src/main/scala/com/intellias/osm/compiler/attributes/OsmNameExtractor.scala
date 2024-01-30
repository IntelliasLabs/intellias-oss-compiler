package com.intellias.osm.compiler.attributes

import com.intellias.osm
import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.{keyPattern, optional}
import com.intellias.osm.compiler.attributes.keys.OsmLanguage.{LanguageCode, LocalLanguage}
import com.intellias.osm.compiler.attributes.keys.{OsmLanguage, OsmNameType, OsmSide, ValuesSeparator}
import com.intellias.osm.compiler.language.{Language, LanguageResolver, LanguageService}
import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.name.Name
import play.api.libs.json.Json

trait OsmNameExtractor extends LanguageResolver with Serializable {
  val tag: String = OsmNameExtractor.tag
  val env: LanguageService

  private val KeyPattern = keyPattern(
    OsmNameType,
    optional(OsmSide),
    optional(OsmLanguage)
  )

  def decodeNames(tags: Map[String, String], adminPlace: Option[FeatureAdminPlace]): Seq[Name] = {
    tags.flatMap {
      case (key, value) =>
        KeyPattern.findFirstMatchIn(key).map(rm => (OsmNameType(rm), OsmSide(rm), OsmLanguage(rm), value))
    }.flatMap {
      case (nameType, side, LanguageCode(langCode), nameStr) =>
        getLanguage(env.langService)(langCode, adminPlace)
          .map(lang => buildNames(lang, side, nameStr, nameType, isDefault = false, isOfficialLanguage(env.langService)(langCode, adminPlace)))
      case (nameType, side, LocalLanguage, nameStr) =>
        Some(buildNames(getRegionOrDefaultLang(env.langService)(adminPlace), side, nameStr, nameType, OsmNameType.Name == nameType, isOfficial = true))
      case _ => None
    }.flatten
  }.toSeq

  private def buildNames(language: Language, osmSide: OsmSide, nameStr: String, osmNameType: OsmNameType, isDefault: Boolean, isOfficial: Boolean): Seq[Name] = {
      nameStr.split(ValuesSeparator).zipWithIndex.map { case (name, index) =>
      osm.model.name.Name(name, language.isoLanguageCode, language.langId, osmNameType.nameType, isDefault && index == 0, isOfficial, osmSide.side)
    }
  }
}

object OsmNameExtractor {
  val tag: String = "NDS:Name"

  def getMainName(tags: Map[String, String]): Option[String] = {
    tags
      .get(OsmNameExtractor.tag)
      .map(json => Json.parse(json).as[Seq[Name]])
      .flatMap(names => names.find(_.isDefault))
      .map(_.name)
  }
}
