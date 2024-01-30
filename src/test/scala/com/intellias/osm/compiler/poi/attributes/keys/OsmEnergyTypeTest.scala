package com.intellias.osm.compiler.poi.attributes.keys

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._

class OsmEnergyTypeTest extends AnyFunSpec with Matchers {

  it("should serialize/deserialize OsmEnergyType correctly") {
    val energyTypes = OsmFuelType.values :+ OsmFuelType.Electricity
    val json        = Json.toJson(energyTypes).toString()
    Json.parse(json).as[Seq[OsmFuelType]] should contain theSameElementsAs energyTypes
  }

}
