package com.intellias.osm.model.common

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class FeatureRangeTest extends AnyFunSpec with Matchers {
  it("should serialize/deserialize FeatureRange.Complete correctly") {
    val complete: FeatureRange = FeatureRange.Complete
    val json = Json.toJson(complete).toString()
    Json.parse(json).as[FeatureRange] shouldBe complete
  }

  it("should serialize/deserialize FeatureRange.Position correctly") {
    val position: FeatureRange = FeatureRange.PositionRange(Wgs84Coordinate(1.0, 1.0), Wgs84Coordinate(0.0, 0.0))
    val json = Json.toJson(position).toString()
    Json.parse(json).as[FeatureRange] shouldBe position
  }
}
