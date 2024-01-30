package com.intellias.osm.model.road

import com.intellias.osm.model.common.DirectionType
import play.api.libs.json.{Format, Json}

case class Construction(element: Option[String], value: String)

object Construction {
  implicit val format: Format[Construction] = Json.format[Construction]
}
