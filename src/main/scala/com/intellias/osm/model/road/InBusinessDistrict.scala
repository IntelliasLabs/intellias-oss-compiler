package com.intellias.osm.model.road

import com.intellias.osm.model.common.FeatureRange
import play.api.libs.json.{Format, Json}

case class InBusinessDistrict(ranges: Seq[FeatureRange])

object InBusinessDistrict {
  implicit val format: Format[InBusinessDistrict] = Json.format[InBusinessDistrict]
}
