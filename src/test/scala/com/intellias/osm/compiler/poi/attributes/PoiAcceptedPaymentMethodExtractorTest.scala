package com.intellias.osm.compiler.poi.attributes

import com.intellias.osm.compiler.poi.PoiEnvironmentDummy
import com.intellias.osm.model.poi.{POI, Payment}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PoiAcceptedPaymentMethodExtractorTest extends AnyFunSpec with Matchers with PoiEnvironmentDummy {

  it("should parse payment method"){
    val poi: POI = creatDummyPoi(Map(
      "payment:mastercard" -> "yes",
      "payment:credit_cards" -> "yes",
      "payment:visa" -> "yes",
      "payment:cash" -> "yes",
      "payment:НашаКартаКлієнта" -> "yes"
    ))


    PoiAcceptedPaymentMethodExtractor(poiEnv).decodeFromOsm(poi) should contain theSameElementsAs Seq(
      Payment("НашаКартаКлієнта", getGlobalLanguage("ukr")),
      Payment("Credit card", getGlobalLanguage("eng")),
      Payment("Кредитна картка", getGlobalLanguage("ukr")),
      Payment("Cash", getGlobalLanguage("eng")),
      Payment("Готівка", getGlobalLanguage("ukr")),
      Payment("MasterCard", getGlobalLanguage("eng")),
      Payment("Visa", getGlobalLanguage("eng"))
    )

  }
}
