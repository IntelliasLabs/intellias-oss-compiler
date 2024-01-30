package com.intellias.osm.model.road

import com.intellias.osm.model.common.FeatureRange
import play.api.libs.json.{Format, Json}

case class RoadCharacteristics(characterTypes: Seq[RoadCharacterType], range: FeatureRange)

object RoadCharacteristics {
  implicit val format: Format[RoadCharacteristics] = Json.format[RoadCharacteristics]
}
