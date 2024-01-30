package com.intellias.osm.compiler.language

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class Iso639Test extends AnyFunSpec with Matchers {
  it("should parse language properly") {
    toLangCode("fr") shouldBe Some("fra")
    toLangCode("fra") shouldBe Some("fra")
    toLangCode("it") shouldBe Some("ita")
    toLangCode("lb") shouldBe Some("ltz")
    toLangCode("fra") shouldBe Some("fra")
    toLangCode("fr") shouldBe Some("fra")
    toLangCode("uk") shouldBe Some("ukr")
    toLangCode("ukr") shouldBe Some("ukr")
  }

  def toLangCode(str: String): Option[String] = str match {
    case Iso639(langCode) => Some(langCode.alpha3)
    case _ => None
  }
}
