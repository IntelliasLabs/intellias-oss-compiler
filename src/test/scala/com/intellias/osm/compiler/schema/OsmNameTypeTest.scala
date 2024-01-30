package com.intellias.osm.compiler.schema

import com.intellias.osm.compiler.attributes.keys.OsmNameType
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class OsmNameTypeTest extends AnyFunSpec with Matchers {

  it("should serialize/deserialize NameType correctly") {
    val nameType = OsmNameType.values
    val json = Json.toJson(nameType).toString()
    Json.parse(json).as[Seq[OsmNameType]] should contain theSameElementsAs nameType
  }
}
