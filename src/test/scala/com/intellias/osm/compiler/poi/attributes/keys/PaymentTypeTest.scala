package com.intellias.osm.compiler.poi.attributes.keys

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class PaymentTypeTest extends AnyFunSpec with Matchers {
  it("should serialize/deserialize PaymentType correctly") {
    val languages: Seq[PaymentType] = PaymentType.values :+ PaymentType.LocalPayment("Some Payment")
    val json = Json.toJson(languages).toString()
    Json.parse(json).as[Seq[PaymentType]] should contain theSameElementsAs languages
  }
}
