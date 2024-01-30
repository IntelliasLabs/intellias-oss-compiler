package com.intellias.osm.compiler.poi.attributes

import com.intellias.osm.compiler.poi.PoiEnvironmentDummy
import com.intellias.osm.model.poi.{Brand, POI}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PoiBrandNameExtractorTest extends AnyFunSpec with Matchers with PoiEnvironmentDummy {
  it("should decode brands correctly"){
    val srcPoi: POI = creatDummyPoi(Map(
      "brand" -> "Вишиванка",
      "brand:en" -> "Vyshyvanka",
      "brand:ukr" -> "Вишиванка",
      "brand:bbb" -> "Something"
    ))

    val brands = PoiBrandNameExtractor(poiEnv).decodeFromOsm(srcPoi)

    val expected = Seq(
      Brand("Вишиванка", poiEnv.langService.globalDefault("ukr").get.langId),
      Brand("Vyshyvanka", poiEnv.langService.globalDefault("eng").get.langId),
      Brand("Вишиванка", poiEnv.langService.globalDefault("ukr").get.langId)
    )

    brands should contain theSameElementsAs expected
  }
}
