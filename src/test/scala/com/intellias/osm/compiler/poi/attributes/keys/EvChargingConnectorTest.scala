package com.intellias.osm.compiler.poi.attributes.keys

import com.intellias.osm.model.poi.{EvChargingConnector, SocketType}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class EvChargingConnectorTest extends AnyFunSpec with Matchers {
  it("should serialize/deserialize OsmEnergyType correctly") {
    val connector = EvChargingConnector(socketType = SocketType.IEC62196_2_T2O, socketPower = 100, voltage = 300, countSockets = 3)
    val json = Json.toJson(connector).toString()
    Json.parse(json).as[EvChargingConnector] shouldBe connector
  }
}
