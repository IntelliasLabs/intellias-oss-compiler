package com.intellias.osm.compiler.display.attribute.value.area.building

import com.intellias.osm.compiler.display.attribute.SrcDisplayAreaAttribute
import play.api.libs.json.{Format, Json}

case class Height(value: Float) extends SrcDisplayAreaAttribute

object Height {
  implicit val format: Format[Height] = Json.format[Height]
}
