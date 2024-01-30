package com.intellias.osm.compiler.display.attribute.extractor.area

import com.intellias.osm.compiler.display.attribute.extractor.area.building.RoofColorExtractor
import com.intellias.osm.compiler.display.attribute.value.area.Color
import com.intellias.osm.compiler.display.{DisplayEnvironmentMock, DisplayMocker}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ColorExtractorTest extends AnyFunSpec with Matchers with DisplayMocker with DisplayEnvironmentMock {
  describe("Color extractor should recognize color") {
    it("by its name") {
      val color = decodeColor("yellow").head
      color should be(Color.Yellow)
    }

    it("by its hex code") {
      val color = decodeColor("#ffffff").head
      color should be(Color.White)
    }

    it("regardless of the case used for color name") {
      val colorLowerCase = decodeColor("brown").head
      val colorUpperCase = decodeColor("BROWN").head
      val colorMixedCase = decodeColor("Brown").head
      colorLowerCase should be(Color.Brown)
      colorUpperCase should be(Color.Brown)
      colorMixedCase should be(Color.Brown)
    }

    it("specified with different pronunciation") {
      val color1 = decodeColor("grey").head
      val color2 = decodeColor("gray").head
      color1 should be (Color.Grey)
      color2 should be (Color.Grey)
    }

    it("or return nothing in case if unknown color is provided") {
      val colors = decodeColor("Unknown")
      colors shouldBe empty
    }
  }

  private def decodeColor(osmValue: String): Seq[Color] = {
    RoofColorExtractor.decodeFromOsm(mockDisplayArea(Map("building:colour" -> osmValue)))
  }
}
