package com.intellias.osm.model.road

import com.intellias.osm.model.common.FeatureRange
import play.api.libs.json.{Format, Json}

case class RailwayCrossingGate(gateType: RailwayCrossingGateType, ranges: Seq[FeatureRange])

object RailwayCrossingGate {
  implicit val format: Format[RailwayCrossingGate] = Json.format[RailwayCrossingGate]
}
