package com.intellias.osm.compiler.schema.model.road

import play.api.libs.json.{Format, Json, Writes}

case class PhysicalWidth(width: Int)

object PhysicalWidth {
  implicit val format: Format[PhysicalWidth] = Json.format[PhysicalWidth]
}
