package com.intellias.osm.compiler.poi.attributes
import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.{keyPattern, optional}
import com.intellias.osm.compiler.attributes.keys.OsmLanguage.{LanguageCode, LocalLanguage}
import com.intellias.osm.compiler.attributes.keys.{OsmLanguage, ValuesSeparator}
import com.intellias.osm.compiler.language.{Language, LanguageResolver}
import com.intellias.osm.compiler.poi.PoiEnvironment
import com.intellias.osm.model.poi.{Brand, POI}
import play.api.libs.json.Writes


case class PoiBrandNameExtractor(env: PoiEnvironment) extends PoiAttributeExtractor[Brand] with LanguageResolver {
  override val tag: String = PoiBrandNameExtractor.tag
  override implicit def writes: Writes[Seq[Brand]] = Writes.seq

  private val KeyPattern = keyPattern(
    "brand",
    optional(OsmLanguage)
  )

  def decodeFromOsm(poi: POI): Seq[Brand] = {
    poi.tags.flatMap {
      case (key, value) => KeyPattern.findFirstMatchIn(key).map(rm => (OsmLanguage(rm), value))
    }.flatMap {
      case (LanguageCode(langCode), brands) =>
        getLanguage(env.langService)(langCode, poi.adminPlace)
          .map(lang => createBrands(lang, brands))
      case (LocalLanguage, brands) =>
        Some(createBrands(getRegionOrDefaultLang(env.langService)(poi.adminPlace), brands))
      case _ => None
    }.flatten
  }.toSeq


  private def createBrands(language: Language, brandNames: String): Seq[Brand] =
    brandNames.split(ValuesSeparator).map(name => Brand(name, language.langId))
}

object PoiBrandNameExtractor {
  val tag: String = "NDS:Brand"
}
