package com.intellias.osm.compiler.road.characteristic.area

import com.intellias.mobility.geo.tools.jts.WktUtils
import com.intellias.osm.compiler.road.FeatureArea
import com.intellias.osm.compiler.road.characteristic.TopologySpec
import com.intellias.osm.model.common.FeatureRange
import com.intellias.osm.model.common.wrapper.MultiPolygonWrapper
import com.intellias.osm.model.road.Topology
import org.locationtech.jts.geom.{LineString, Polygon}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RoadAreaCharacteristicsExtractorTest extends AnyFunSpec with Matchers {
  val line: LineString = WktUtils.wktToGeometry("LINESTRING(6.147059365906102 49.57062739825713,6.146520970823901 49.570476217632546,6.1457938599399995 49.57025304537811,6.145499685307101 49.57006946743667,6.144950189295372 49.5697671022628,6.144545005368201 49.569619518580794,6.144078766327852 49.569504331007295,6.143568123569452 49.56945393635837,6.142874315473222 49.56941434052632)")
  val fullPolygon: Polygon = WktUtils.wktToGeometry("POLYGON((6.142443937506272 49.57019372084494,6.1425332452519115 49.568763137649626,6.145480400817263 49.56867625877746,6.148195356247527 49.56973037865174,6.147722025202853 49.57106826725021,6.145132100614575 49.57102772571031,6.142443937506272 49.57019372084494))")
  val halfPolygon1: Polygon = WktUtils.wktToGeometry("POLYGON((6.142524314477413 49.569979425627025,6.142470729829511 49.56904694088831,6.142970853198875 49.56868784263594,6.14434619246353 49.56868784263594,6.145659016305814 49.569099067834344,6.145373231524104 49.56975933766765,6.144882038929211 49.57017055383977,6.144051476906014 49.570153178578664,6.142524314477413 49.569979425627025))")
  val halfPolygon2: Polygon = WktUtils.wktToGeometry("POLYGON((6.144676631117392 49.56999680094998,6.145248200682146 49.56927282391965,6.1471058017659175 49.569238072752114,6.147909571465846 49.57016476208676,6.147855986819253 49.5708829342214,6.14547147004285 49.57094664252941,6.144676631117392 49.56999680094998))")
  val topology: Topology = TopologySpec.mapToTopology(
    Map.empty,
    line.getCoordinates.map(c => TopologySpec.createDummyNode(lonLat = (c.x, c.y)))
  )

  it("should create complete range with full and half area") {
    val areas = List(toTopoArea(1, fullPolygon), toTopoArea(2, halfPolygon1))
    RangeBuilder.findRanges(topology, areas) shouldBe List(FeatureRange.Complete)
  }


  //TODO: fix RangeMerger to handle imperfect intersect calculation.
//  it("should create complete range from two hals areas") {
//    val areas = List(toTopoArea(1, halfPolygon2), toTopoArea(2, halfPolygon1))
//    RangeMerger.toRanges(topology, areas) shouldBe List(FeatureRange.Complete)
//  }

  def toTopoArea(id: Long, polygon: Polygon): FeatureArea = {
    FeatureArea(id, Map.empty[String, String], "way", MultiPolygonWrapper.fromJTS(Seq(polygon)))
  }
}
