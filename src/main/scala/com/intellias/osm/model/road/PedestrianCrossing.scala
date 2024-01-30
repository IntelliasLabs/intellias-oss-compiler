package com.intellias.osm.model.road

import com.intellias.osm.model.common.FeatureRange
import play.api.libs.json.{Format, Json}

case class PedestrianCrossing(ranges: Seq[FeatureRange])

object PedestrianCrossing {
  implicit val format: Format[PedestrianCrossing] = Json.format[PedestrianCrossing]
}
