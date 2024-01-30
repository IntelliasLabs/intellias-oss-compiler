package com.intellias.osm.compiler.poi.attributes

import com.intellias.osm.compiler.poi.PoiEnvironmentDummy
import com.intellias.osm.model.poi.{EvChargingConnector, POI, SocketType}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.language.{existentials, postfixOps}

class PoiEvChargingDetailsExtractorTest extends AnyFunSpec with Matchers with PoiEnvironmentDummy {

  it("should create Charging Details"){
    val srcPoi: POI = creatDummyPoi(Map(
      "socket:type2" -> "2",
      "socket:type2:output" -> "22 kW",
      "socket:type2:voltage" -> "600 V"
      ))

    val expected = Seq(EvChargingConnector(socketType = SocketType.IEC62196_2_T2O, socketPower = 22000, voltage = 600, countSockets = 2))

    val result = PoiEvChargingDetailsExtractor.decodeFromOsm(srcPoi)

    result should contain allElementsOf expected

  }
}
