package com.intellias.osm.compiler.road.rules.values

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class OsmVehicleTypeTest extends AnyFunSpec with Matchers {
  it("should serialize/deserialize OsmVehicleType correctly") {
    val socketTypes = OsmVehicleType.values :+ OsmVehicleType.All
    val json = Json.toJson(socketTypes).toString()
    Json.parse(json).as[Seq[OsmVehicleType]] should contain theSameElementsAs socketTypes
  }
}
