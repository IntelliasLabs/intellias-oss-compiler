package com.intellias.osm.compiler.road.characteristic

import com.intellias.osm.compiler.road.characteristic.TopologySpec._
import com.intellias.osm.compiler.road.characteristic.range.RoadTypeFormExtractor
import com.intellias.osm.compiler.road.highway
import com.intellias.osm.model.road.RoadFormType
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RoadFormTypeFormExtractorTest extends AnyFunSpec with Matchers {

  describe("isPedestrian") {
    describe("isPedestrian") {
      it("should be pedestrian when highway is either footway, steps or corridor") {
        List("footway", "steps", "corridor").foreach { hw =>
          RoadTypeFormExtractor.decodeFromOsm(Map(highway -> hw)) shouldBe Seq(RoadFormType.PedestrianWay)
        }
      }

      it("should be pedestrian when highway is cycleway and foot is designated") {
        RoadTypeFormExtractor.decodeFromOsm(Map(highway -> "cycleway", "foot" -> "designated")) shouldBe Seq(RoadFormType.PedestrianWay)
      }

      it("should be pedestrian when highway is path and foot is designated") {
        RoadTypeFormExtractor.decodeFromOsm(Map(highway -> "path", "foot" -> "designated")) shouldBe Seq(RoadFormType.PedestrianWay)
      }
    }
  }

  describe("isRoundAbout") {
    it("should be roundabout") {
      List("roundabout", "mini_roundabout").foreach { junc =>
        RoadTypeFormExtractor.decodeFromOsm(Map("junction" -> junc)) shouldBe Seq(RoadFormType.Roundabout)
      }
    }
  }

  describe("isSlipRoad") {
    it("should be slip road") {
      List("trunk_link", "primary_link", "secondary_link", "tertiary_link").foreach { link =>
        RoadTypeFormExtractor.decodeFromOsm(Map(highway -> link)) shouldBe Seq(RoadFormType.SlipRoad)
      }
    }
  }

  describe("isMotorwayRamp") {
    it("should be ramp") {
      RoadTypeFormExtractor.decodeFromOsm(Map(highway -> "motorway_link")) shouldBe Seq(RoadFormType.Ramp)
    }
  }

  describe("isServiceRoad") {
    it("should identify service road by 'frontage_road' tag") {
      val tags: Map[String, String] = Map(highway -> "any_value", "frontage_road" -> "yes")
      RoadTypeFormExtractor.decodeFromOsm(tags) shouldBe Seq(RoadFormType.ServiceRoad)
    }

    it("should identify service road by 'side_road' tag") {
      val tags: Map[String, String] = Map(highway -> "any_value", "side_road" -> "yes")
      RoadTypeFormExtractor.decodeFromOsm(tags) shouldBe Seq(RoadFormType.ServiceRoad)
    }

    it("should not identify service road if no highway tag") {
      val tags: Map[String, String] = Map("frontage_road" -> "yes")
      RoadTypeFormExtractor.decodeFromOsm(tags) should not contain RoadFormType.ServiceRoad
    }

    it("should not identify service road if no 'frontage_road' or 'side_road' tag") {
      val tags: Map[String, String] = Map(highway -> "any_value", "another_tag" -> "yes")
      RoadTypeFormExtractor.decodeFromOsm(tags) should not contain RoadFormType.ServiceRoad
    }
  }

  describe("isDualCarriageway") {
    it("should identify dual carriageway by 'dual_carriageway' tag") {
      val tags: Map[String, String] = Map("dual_carriageway" -> "yes")
      RoadTypeFormExtractor.decodeFromOsm(tags) shouldBe Seq(RoadFormType.DualCarriageway)
    }

    it("should identify dual carriageway by 'highway' set to 'motorway' tag") {
      val tags: Map[String, String] = Map(highway -> "motorway")
      RoadTypeFormExtractor.decodeFromOsm(tags) shouldBe Seq(RoadFormType.DualCarriageway)
    }

    it("should identify dual carriageway by 'highway' set to 'trunk' tag") {
      val tags: Map[String, String] = Map(highway -> "trunk")
      RoadTypeFormExtractor.decodeFromOsm(tags) shouldBe Seq(RoadFormType.DualCarriageway)
    }

    it("should identify dual carriageway by 'highway' set to 'primary' tag") {
      val tags: Map[String, String] = Map(highway -> "primary")
      RoadTypeFormExtractor.decodeFromOsm(tags) shouldBe Seq(RoadFormType.DualCarriageway)
    }

    it("should not identify dual carriageway if no relevant tags are present") {
      val tags: Map[String, String] = Map("random_tag" -> "random_value")
      RoadTypeFormExtractor.decodeFromOsm(tags) should not contain RoadFormType.DualCarriageway
    }
  }

  describe("isAny") {
    it("should be any") {
      List("path", "track").foreach { any =>
        RoadTypeFormExtractor.decodeFromOsm(Map(highway -> any)) shouldBe Seq(RoadFormType.Any)
      }
    }
  }
}
