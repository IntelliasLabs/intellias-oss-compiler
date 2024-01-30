package com.intellias.osm.ndslive.name.road

import com.intellias.osm.compiler.schema.model.common.ReferenceType
import com.intellias.osm.compiler.schema.model.road.RoadNumber
import com.intellias.osm.ndslive.name.road.NdsRoadNumberBuilder.PrefixNumberSuffix
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class NdsRoadNumberBuilderTest extends AnyFunSpec with Matchers {

  it("should parse road into prefix, number and suffix") {
    NdsRoadNumberBuilder.toPrefixNumberSuffix(RoadNumber("21", ReferenceType.Reference)) shouldBe PrefixNumberSuffix("21", None, None)
    NdsRoadNumberBuilder.toPrefixNumberSuffix(RoadNumber("21M", ReferenceType.Reference)) shouldBe PrefixNumberSuffix("21", None, Some("M"))
    NdsRoadNumberBuilder.toPrefixNumberSuffix(RoadNumber("A21", ReferenceType.Reference)) shouldBe PrefixNumberSuffix("21", Some("A"), None)
    NdsRoadNumberBuilder.toPrefixNumberSuffix(RoadNumber("A21M", ReferenceType.Reference)) shouldBe PrefixNumberSuffix("21", Some("A"), Some("M"))
  }
}
