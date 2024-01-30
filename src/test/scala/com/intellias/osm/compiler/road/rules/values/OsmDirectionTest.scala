package com.intellias.osm.compiler.road.rules.values

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class OsmDirectionTest extends AnyFunSpec with Matchers {
  it("should serialize/deserialize OsmDirection correctly") {
    val socketTypes = OsmDirection.values
    val json = Json.toJson(socketTypes).toString()
    Json.parse(json).as[Seq[OsmDirection]] should contain theSameElementsAs socketTypes
  }
}
