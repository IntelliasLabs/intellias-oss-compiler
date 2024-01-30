package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.road.characteristic.TopologySpec.mapToTopology
import com.intellias.osm.compiler.road.characteristic.range.RoadHasSidewalkExtractor.decodeFromOsm
import com.intellias.osm.model.common.Side
import com.intellias.osm.model.road.HasSideWalk
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RoadHasSidewalkExtractorTest extends AnyFunSpec with Matchers {

  it("should parse sidewalk properly"){
    decodeFromOsm(mapToTopology(Map("sidewalk" -> "both"))) shouldBe Seq(HasSideWalk(Side.Both))
    decodeFromOsm(mapToTopology(Map("sidewalk" -> "left"))) shouldBe Seq(HasSideWalk(Side.Left))
    decodeFromOsm(mapToTopology(Map("sidewalk" -> "right"))) shouldBe Seq(HasSideWalk(Side.Right))

    decodeFromOsm(mapToTopology(Map("sidewalk:left" -> "yes"))) shouldBe Seq(HasSideWalk(Side.Left))
    decodeFromOsm(mapToTopology(Map("sidewalk:right" -> "yes"))) shouldBe Seq(HasSideWalk(Side.Right))
    decodeFromOsm(mapToTopology(Map("sidewalk:both" -> "yes"))) shouldBe Seq(HasSideWalk(Side.Both))

    decodeFromOsm(mapToTopology(Map("sidewalk:left" -> "no"))) shouldBe Seq()
    decodeFromOsm(mapToTopology(Map("sidewalk:right" -> "none"))) shouldBe Seq()
    decodeFromOsm(mapToTopology(Map("sidewalk:both" -> "no"))) shouldBe Seq()
    decodeFromOsm(mapToTopology(Map("sidewalk" -> "no"))) shouldBe Seq()

    decodeFromOsm(mapToTopology(Map())) shouldBe Seq()
  }
}
