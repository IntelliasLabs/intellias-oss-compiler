package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.road.characteristic.TopologySpec.mapToTopology
import com.intellias.osm.compiler.road.characteristic.range.RoadStartOrDestinationRoadOnlyExtractor.decodeFromOsm
import com.intellias.osm.model.road.StartOrDestinationRoadFlag
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RoadStartOrDestinationRoadOnlyExtractorTest extends AnyFunSpec with Matchers {

  it("should parse start or destination roads correctly") {
    decodeFromOsm(mapToTopology(Map("access" -> "private"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "customers"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "no"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "permissive"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "destination"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "agricultural"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "forestry"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "permit"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "unknown"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "delivery"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "military"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "restricted"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "emergency"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "residents"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "official"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "employees"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "license"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "visitors"))) shouldBe Seq(StartOrDestinationRoadFlag())
    decodeFromOsm(mapToTopology(Map("access" -> "members"))) shouldBe Seq(StartOrDestinationRoadFlag())

    decodeFromOsm(mapToTopology(Map("access" -> "public"))) shouldBe Seq()
  }

}
