package com.intellias.osm.compiler.poi.attributes.keys

import com.intellias.osm.compiler.attributes.keys.OsmLanguage
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class OsmLanguageTest extends AnyFunSpec with Matchers {
  it("should serialize/deserialize OsmLanguage correctly") {
    val languages: Array[OsmLanguage] = Array(OsmLanguage.LocalLanguage, OsmLanguage.NotALanguage, OsmLanguage.LanguageCode("eng"))
    val json = Json.toJson(languages).toString()
    Json.parse(json).as[Seq[OsmLanguage]] should contain theSameElementsAs languages
  }
}
