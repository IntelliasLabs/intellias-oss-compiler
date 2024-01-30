package com.intellias.osm.compiler.schema

import com.intellias.osm.compiler.attributes.keys.OsmSide
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class OsmSideTest extends AnyFunSpec with Matchers {

  it("should serialize/deserialize Side correctly") {
    val nameType = OsmSide.values ++ OsmSide.extraValuesForReaders
    val json = Json.toJson(nameType).toString()
    Json.parse(json).as[Seq[OsmSide]] should contain theSameElementsAs nameType
  }
}
