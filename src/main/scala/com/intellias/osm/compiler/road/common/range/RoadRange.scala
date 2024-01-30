package com.intellias.osm.compiler.road.common.range

import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo, JsonTypeName}
import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.osm.model.common.FeatureRange

@deprecated
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "t"
)
@JsonSubTypes(Array(
  new JsonSubTypes.Type(value = classOf[RoadPositionRanges], name = "p"),
  new JsonSubTypes.Type(value = classOf[RoadRangeComplete], name = "c")
))
sealed trait RoadRange

@deprecated
@JsonTypeName("p")
case class RoadPositionRanges(ranges: List[PositionRange]) extends RoadRange

@deprecated
@JsonTypeName("c")
case class RoadRangeComplete() extends RoadRange

@deprecated
case class PositionRange(start: Wgs84Coordinate, end: Wgs84Coordinate)

@deprecated
object RoadRange {
  implicit class RoadRangeImplicits(list: List[RoadRange]) {
    def merge: RoadRange = list match {
      case _ if list.exists(_.isInstanceOf[RoadRangeComplete]) => RoadRangeComplete()
      case (h@RoadPositionRanges(_)) :: rest => rest.fold(h) {
        case (r1: RoadPositionRanges, r2: RoadPositionRanges) => r1.copy(ranges = r1.ranges ++ r2.ranges)
        case (r1, _) => r1
      }
      case _ => RoadRangeComplete()
    }
  }

  implicit class FeatureRangeImplicits(featureRange: FeatureRange) {
    def toRoadRange: RoadRange = featureRange match {
      case FeatureRange.Complete => RoadRangeComplete()
      case FeatureRange.PositionRange(start, end) => RoadPositionRanges(List(PositionRange(start, end)))
    }
  }
}
