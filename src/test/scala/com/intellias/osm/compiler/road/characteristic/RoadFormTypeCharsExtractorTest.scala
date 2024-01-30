package com.intellias.osm.compiler.road.characteristic

import com.intellias.osm.compiler.road.characteristic.TopologySpec._
import com.intellias.osm.compiler.road.characteristic.range.RoadTypeCharsExtractor.decodeFromOsm
import com.intellias.osm.compiler.road.highway
import com.intellias.osm.model.road.{RoadCharacterType, RoadCharacteristics}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RoadFormTypeCharsExtractorTest extends AnyFunSpec with Matchers {

  describe("isParking") {
    it("should be parking") {
      toCharTypes(decodeFromOsm(Map(highway->"service", "service"->"parking_aisle"))) should contain (RoadCharacterType.Parking)
    }

    it("should not identify parking road if missed 'service'->'parking_aisle' tag") {
      val tags: Map[String, String] = Map(highway -> "service", "another_tag" -> "yes")
      toCharTypes(decodeFromOsm(tags)) should not contain RoadCharacterType.Parking
    }
  }

  describe("isMotorway") {
    it("should be motorway") {
      toCharTypes(decodeFromOsm(Map("highway" -> "motorway"))) should contain (RoadCharacterType.Motorway)
      toCharTypes(decodeFromOsm(Map("highway" -> "motorway_link"))) should contain (RoadCharacterType.Motorway)
    }

    it("should not identify motorway road if missed 'highway'->'motorway' or 'highway'->'motorway_link' tag") {
      val tags: Map[String, String] = Map("highway" -> "any_value", "another_tag" -> "yes")
      toCharTypes(decodeFromOsm(tags)) should not contain RoadCharacterType.Motorway
    }
  }

  describe("isTunnel") {
    it("should be tunnel") {
      toCharTypes(decodeFromOsm(Map("tunnel" -> "any_value"))) should contain (RoadCharacterType.Tunnel)
    }
  }

  describe("isBridge") {
    it("should be bridge") {
      toCharTypes(decodeFromOsm(Map("bridge" -> "any_value"))) should contain (RoadCharacterType.Bridge)
    }
  }

  describe("isRace") {
    it("should be race") {
      toCharTypes(decodeFromOsm(Map("highway" -> "raceway"))) should contain (RoadCharacterType.RaceTrack)
    }
  }

  describe("isTracked") {
    it("should be tracked") {
      toCharTypes(decodeFromOsm(Map("railway" -> "tram"))) should contain (RoadCharacterType.TracksOnRoad)
    }
  }

  describe("isBusOnly") {
    it("should be bus only") {
      toCharTypes(decodeFromOsm(Map("highway" -> "busway"))) should contain (RoadCharacterType.BusRoad)
      toCharTypes(decodeFromOsm(Map("highway" -> "service", "bus" -> "designated"))) should contain (RoadCharacterType.BusRoad)
    }
  }

  private def toCharTypes(characteristics:  Seq[RoadCharacteristics]): Seq[RoadCharacterType] = characteristics.flatMap(_.characterTypes)
}