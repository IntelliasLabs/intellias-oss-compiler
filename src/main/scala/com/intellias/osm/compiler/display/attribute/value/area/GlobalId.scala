package com.intellias.osm.compiler.display.attribute.value.area

import com.intellias.osm.compiler.display.attribute.SrcDisplayAreaAttribute
import play.api.libs.json.{Format, Json}

case class GlobalId(id: String) extends SrcDisplayAreaAttribute

object GlobalId {
  implicit val format: Format[GlobalId] = Json.format[GlobalId]
}
