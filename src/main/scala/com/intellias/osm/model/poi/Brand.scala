package com.intellias.osm.model.poi

import play.api.libs.json._


case class Brand(name: String, langId: Short)

object Brand {
  implicit val format: Format[Brand] = Json.format[Brand]
}