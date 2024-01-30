package com.intellias.osm.compiler.display.attribute.value.point

import com.intellias.osm.compiler.display.attribute.SrcDisplayPointAttribute
import play.api.libs.json.{Format, Json}

case class Population(value: Int) extends SrcDisplayPointAttribute

object Population {
  implicit val format: Format[Population] = Json.format[Population]
}