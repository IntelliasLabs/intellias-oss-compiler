package com.intellias.osm.jts

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.mobility.geo.tools.geotools.GeoTools
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers._


class GeoToolsTest extends AnyFunSpec {

  it("should calculate length of lines") {
    val coords = Array((24.0320955, 49.784770200000004),
      (24.032276300000003, 49.7847425),
      (24.032360800000003, 49.7847425),
      (24.032442600000003, 49.7847486),
      (24.032626200000003, 49.784778700000004),
      (24.032924400000002, 49.7848388),
      (24.033201400000003, 49.784885100000004),
      (24.033382000000003, 49.7849158),
      (24.033605700000003, 49.784930700000004))
      .map(t => Wgs84Coordinate(t._1, t._2))

    val res = GeoTools.calculateLength(coords)

    res should (be > 111.7 and be < 111.8)
  }

  it("should calculate distance between two points"){
    val start = Wgs84Coordinate(24.0320955, 49.784770200000004)
    val end = Wgs84Coordinate(24.033605700000003, 49.784930700000004)

    val distance = GeoTools.calculateDistance(start, end)

    distance should (be > 110.0 and be < 111.0)
  }


}
