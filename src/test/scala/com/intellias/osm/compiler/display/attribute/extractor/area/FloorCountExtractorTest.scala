package com.intellias.osm.compiler.display.attribute.extractor.area

import com.intellias.osm.compiler.display.attribute.extractor.area.building.FloorCountExtractor
import com.intellias.osm.compiler.display.{DisplayEnvironmentMock, DisplayMocker}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FloorCountExtractorTest extends AnyFunSpec with Matchers with DisplayMocker with DisplayEnvironmentMock {
  val tolerance = 0.01f
  describe("Floor Count Extractor parses floor count") {
    it("when it is specified as integer number") {
      val floorCounts = FloorCountExtractor.decodeFromOsm(mockDisplayArea(Map("building:levels" -> "3")))
      floorCounts.length should be(1)
      floorCounts.head.value should be(3f +- tolerance)
    }
    it("when it is specified as float number") {
      val floorCounts = FloorCountExtractor.decodeFromOsm(mockDisplayArea(Map("building:levels" -> "12.0")))
      floorCounts.length should be(1)
      floorCounts.head.value should be(12f +- tolerance)
    }
    it("and does not fail in case if value of OSM tag is not a number") {
      val floorCounts = FloorCountExtractor.decodeFromOsm(mockDisplayArea(Map("building:levels" -> "___")))
      floorCounts shouldBe empty
    }
  }
}
