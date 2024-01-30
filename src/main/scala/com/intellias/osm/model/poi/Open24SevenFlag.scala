package com.intellias.osm.model.poi

import play.api.libs.json.{Format, Json}

case class Open24SevenFlag(isOpen24Seven: Boolean = true)

object Open24SevenFlag {
  implicit val format: Format[Open24SevenFlag] = Json.format[Open24SevenFlag]
}
