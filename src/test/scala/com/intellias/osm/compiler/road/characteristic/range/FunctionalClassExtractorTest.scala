package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.road.RoadEnvironmentDummy
import com.intellias.osm.model.road.{RoadClass, Topology}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class FunctionalClassExtractorTest extends AnyFunSpec with RoadEnvironmentDummy {
  it("should decode Functional class 1 correctly") {
    val feature: Topology = creatDummyTopology(Map("highway" -> "motorway"))
    FunctionalClassExtractor.decodeFromOsm(feature) should contain theSameElementsAs Seq(RoadClass(1))
  }

  it("should decode Functional class 2 correctly") {
    val feature: Topology = creatDummyTopology(Map("highway" -> "trunk"))
    FunctionalClassExtractor.decodeFromOsm(feature) should contain theSameElementsAs Seq(RoadClass(2))
  }

  it("should decode Functional class 3 correctly") {
    val feature: Topology = creatDummyTopology(Map("highway" -> "primary"))
    FunctionalClassExtractor.decodeFromOsm(feature) should contain theSameElementsAs Seq(RoadClass(3))
  }

  it("should decode Functional class 4 correctly") {
    val feature: Topology = creatDummyTopology(Map("highway" -> "secondary"))
    FunctionalClassExtractor.decodeFromOsm(feature) should contain theSameElementsAs Seq(RoadClass(4))
  }

  it("should decode Functional class 5 correctly") {
    val feature: Topology = creatDummyTopology(Map("highway" -> "residential"))
    FunctionalClassExtractor.decodeFromOsm(feature) should contain theSameElementsAs Seq(RoadClass(5))
  }

  it("should decode Functional class 6 correctly") {
    val feature: Topology = creatDummyTopology(Map("highway" -> "road"))
    FunctionalClassExtractor.decodeFromOsm(feature) should contain theSameElementsAs Seq(RoadClass(6))
  }

  it("should decode Functional class 7 correctly") {
    val feature: Topology = creatDummyTopology(Map("highway" -> "some_road"))
    FunctionalClassExtractor.decodeFromOsm(feature) should contain theSameElementsAs Seq(RoadClass(7))
  }
}
