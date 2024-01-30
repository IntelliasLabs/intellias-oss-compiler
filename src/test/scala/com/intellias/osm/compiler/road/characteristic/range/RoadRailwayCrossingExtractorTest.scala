package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.osm.compiler.road.characteristic.TopologySpec.{createDummyNode, mapToTopology}
import com.intellias.osm.compiler.road.characteristic.range.RoadRailwayCrossingExtractor.decodeFromOsm
import com.intellias.osm.compiler.road.highway
import com.intellias.osm.model.common.FeatureRange.PositionRange
import com.intellias.osm.model.road.{MovableBridgeFlag, RailwayCrossingGate, RailwayCrossingGateType}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RoadRailwayCrossingExtractorTest extends AnyFunSpec with Matchers {

  it ("should parse railway crossing gates properly"){
    val topology1 = mapToTopology(
      Map(highway -> ""),
      Array(createDummyNode(lonLat = (1.0, 1.0), Map("railway" -> "level_crossing", "crossing:barrier" -> "double_half") ))
    )

    val expectedGate1 = RailwayCrossingGate(RailwayCrossingGateType.DoubleHalf, Seq(PositionRange(Wgs84Coordinate(1.0, 1.0),Wgs84Coordinate(1.0, 1.0))))
    decodeFromOsm(topology1) shouldBe Seq(expectedGate1)

    val topology2 = mapToTopology(
      Map(highway -> ""),
      Array(createDummyNode(lonLat = (1.0, 1.0), Map("railway" -> "level_crossing") ))
    )

    val expectedGate2 = RailwayCrossingGate(RailwayCrossingGateType.Unknown, Seq(PositionRange(Wgs84Coordinate(1.0, 1.0),Wgs84Coordinate(1.0, 1.0))))
    decodeFromOsm(topology2) shouldBe Seq(expectedGate2)

    val topology3 = mapToTopology(
      Map(highway -> ""),
      Array(createDummyNode(lonLat = (1.0, 1.0), Map("railway" -> "level_crossing", "crossing:barrier" -> "no") ))
    )

    val expectedGate3 = RailwayCrossingGate(RailwayCrossingGateType.NoGates, Seq(PositionRange(Wgs84Coordinate(1.0, 1.0),Wgs84Coordinate(1.0, 1.0))))
    decodeFromOsm(topology3) shouldBe Seq(expectedGate3)
  }


}
