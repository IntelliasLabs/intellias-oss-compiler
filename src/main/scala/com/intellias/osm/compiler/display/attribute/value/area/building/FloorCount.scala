package com.intellias.osm.compiler.display.attribute.value.area.building

import com.intellias.osm.compiler.display.attribute.SrcDisplayAreaAttribute
import play.api.libs.json.{Format, Json}

case class FloorCount(value: Float) extends SrcDisplayAreaAttribute

object FloorCount {
  implicit val format: Format[FloorCount] = Json.format[FloorCount]
}