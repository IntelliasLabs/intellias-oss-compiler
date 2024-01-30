package com.intellias.osm.compiler.schema.model.road

import com.intellias.osm.compiler.schema.model.common.ReferenceType
import play.api.libs.json.{Format, Json}

case class RoadNumber(number: String, refType: ReferenceType)

object RoadNumber {
  implicit val format: Format[RoadNumber] = Json.format[RoadNumber]
}
