package com.intellias.osm.compiler.display.attribute.extractor.area

import com.intellias.osm.compiler.display.{DisplayEnvironmentMock, DisplayMocker}
import com.intellias.osm.compiler.display.attribute.extractor.area.building.HeightExtractor
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class HeightExtractorTest extends AnyFunSpec with Matchers with DisplayMocker with DisplayEnvironmentMock {
  val tolerance = 0.01f

  describe("Height Extractor should") {
    describe("extract a building's height") {
      it("when it is specified as integer value") {
        val heights = HeightExtractor.decodeFromOsm(mockDisplayArea(Map("height" -> "4")))
        heights.length should be(1)
        heights.head.value shouldBe (4f +- tolerance)
      }
      it("when it is specified as floating-point value") {
        val heights = HeightExtractor.decodeFromOsm(mockDisplayArea(Map("height" -> "7.2")))
        heights.length should be(1)
        heights.head.value shouldBe (7.2f +- tolerance)
      }
      it("when it is specified with units") {
        val height = HeightExtractor.decodeFromOsm(mockDisplayArea(Map("height" -> "11.4 m"))).head
        height.value shouldBe (11.4f +- tolerance)
      }
      it("when it is specified with units and delimiter used in OSM") {
        val height = HeightExtractor.decodeFromOsm(mockDisplayArea(Map("height" -> "6␣m"))).head
        height.value shouldBe (6f +- tolerance)
      }
    }

    describe("add to a building's height a roof's height when it is is specified separately") {
      val height = HeightExtractor.decodeFromOsm(
        mockDisplayArea(Map("height" -> "5.2", "roof:height" -> "1.5"))
      ).head
      height.value shouldBe (6.7f +- tolerance)
    }

    describe("ignore OSM tag if it exposes data in an unexpected format") {
      val heights = HeightExtractor.decodeFromOsm(mockDisplayArea(Map("height" -> "1␣to␣1.5")))
      heights shouldBe empty
    }
  }
}
