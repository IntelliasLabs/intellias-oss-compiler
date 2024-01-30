package com.intellias.osm.model.road

import com.intellias.osm.compiler.schema.model.road.TrafficCalmingType
import com.intellias.osm.model.common.{DirectionType, FeatureRange}
import play.api.libs.json.{Format, Json}

case class TrafficCalming(calmingType: TrafficCalmingType, direction: DirectionType, ranges: Seq[FeatureRange])

object TrafficCalming {
  implicit val format: Format[TrafficCalming] = Json.format[TrafficCalming]
}
