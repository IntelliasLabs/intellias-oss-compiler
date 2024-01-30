package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.road.RoadEnvironmentDummy
import com.intellias.osm.model.road.{PavementType, RoadClass, Topology}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper


class RoadPavementExtractorTest extends AnyFunSpec with RoadEnvironmentDummy {
  it("should decode Pavement type: asphalt correctly") {
    val feature: Topology = creatDummyTopology(Map("surface" -> "asphalt"))
    RoadPavementExtractor.decodeFromOsm(feature) should contain theSameElementsAs Seq(PavementType.Asphalt)
  }

  it("should decode Pavement type: concrete correctly") {
    val feature: Topology = creatDummyTopology(Map("surface" -> "concrete"))
    RoadPavementExtractor.decodeFromOsm(feature) should contain theSameElementsAs Seq(PavementType.Concrete)
  }

  it("should decode Pavement type: unpaved correctly") {
    val feature: Topology = creatDummyTopology(Map("surface" -> "unpaved"))
    RoadPavementExtractor.decodeFromOsm(feature) should contain theSameElementsAs Seq(PavementType.Unpaved)
  }
}
