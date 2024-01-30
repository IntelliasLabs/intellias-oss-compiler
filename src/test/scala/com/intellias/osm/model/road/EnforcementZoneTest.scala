package com.intellias.osm.model.road

import com.intellias.osm.model.common.DirectionType
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.Json

class EnforcementZoneTest extends AnyFunSpec{

  it("must be properly serialized/deserialized"){
    val timeRangeOfDay = EnforcementZone(EnforcementType.DangerZone,0,DirectionType.Backward)
    Json.parse(Json.toJson(timeRangeOfDay).toString()).as[EnforcementZone] shouldBe timeRangeOfDay
  }

}
