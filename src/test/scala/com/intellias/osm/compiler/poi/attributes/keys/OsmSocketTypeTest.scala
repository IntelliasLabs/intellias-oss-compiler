package com.intellias.osm.compiler.poi.attributes.keys

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class OsmSocketTypeTest extends AnyFunSpec with Matchers {
  it("should serialize/deserialize SocketType correctly") {
    val socketTypes = OsmSocketType.values
    val json = Json.toJson(socketTypes).toString()
    Json.parse(json).as[Seq[OsmSocketType]] should contain theSameElementsAs socketTypes
  }

}
