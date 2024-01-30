package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.road.characteristic.TopologySpec.mapToTopology
import com.intellias.osm.compiler.road.characteristic.range.RoadMovableBridgeExtractor.decodeFromOsm
import com.intellias.osm.model.road.MovableBridgeFlag
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RoadMovableBridgeExtractorTest extends AnyFunSpec with Matchers {

  it("should parse movable bridge correctly"){
    decodeFromOsm(mapToTopology(Map("bridge:movable" -> ""))) shouldBe Seq(MovableBridgeFlag())
    decodeFromOsm(mapToTopology(Map("bridge" -> ""))) shouldBe Seq()
  }
}
