package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.road.characteristic.TopologySpec.mapToTopology
import com.intellias.osm.compiler.road.characteristic.range.RoadSharedRoadSurfaceWithPedestriansExtractor.decodeFromOsm
import com.intellias.osm.compiler.road.highway
import com.intellias.osm.model.road.{MovableBridgeFlag, RoadSurfaceWithPedestriansFlag}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RoadSharedRoadSurfaceWithPedestriansExtractorTest extends AnyFunSpec with Matchers {

  it("should parse shared surface correctly") {
    decodeFromOsm(mapToTopology(Map(highway -> "residential"))) shouldBe Seq(RoadSurfaceWithPedestriansFlag())
    decodeFromOsm(mapToTopology(Map(highway -> "living_street"))) shouldBe Seq(RoadSurfaceWithPedestriansFlag())
    decodeFromOsm(mapToTopology(Map(highway -> "secondary", "foot" -> "yes"))) shouldBe Seq(RoadSurfaceWithPedestriansFlag())

    decodeFromOsm(mapToTopology(Map(highway -> "secondary"))) shouldBe Seq()
  }
}
