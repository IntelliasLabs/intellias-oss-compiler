package com.intellias.osm.compiler.schema

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.poi.PoiEnvironmentDummy
import com.intellias.osm.model.common.Side
import com.intellias.osm.model.name
import com.intellias.osm.model.name.{Name, NameType}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OsmNameExtractorTest extends AnyFunSpec with Matchers with PoiEnvironmentDummy {
  val nameExtractor: OsmNameExtractor = new OsmNameExtractor {
    override val env: LanguageService = poiEnv
  }

  it("should parse names correctly"){
    val tags: Map[String, String] = Map(
      "name" -> "Lviv",
      "name:left" -> "LvivLeft",
      "name:right" -> "LvivRight",
      "name:uk" -> "Львів",
      "name:en" -> "Lviv",
      "short_name" -> "Lv",
      "payment:НашаКартаКлієнта" -> "yes"
    )

    val adminPlace = dummyAdminPlace

    val res = nameExtractor.decodeNames(tags, adminPlace)
    res should not be empty

    res should contain theSameElementsAs Seq(
      Name("Lviv", "ukr", 5, NameType.Name, isDefault = true, isOfficial = true),
      Name("LvivLeft", "ukr", 5, NameType.Name, isDefault = true, isOfficial = true, Side.Left),
      name.Name("LvivRight", "ukr", 5, NameType.Name, isDefault = true, isOfficial = true, Side.Right),
      Name("Львів", "ukr", 5, NameType.Name, isDefault = false, isOfficial = true),
      Name("Lv", "ukr", 5, NameType.ShortName, isDefault = false, isOfficial = true),
      Name("Lviv", "eng", 1, NameType.Name, isDefault = false, isOfficial = false)
    )
  }
}
