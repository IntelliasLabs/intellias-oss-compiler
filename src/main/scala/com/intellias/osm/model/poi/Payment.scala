package com.intellias.osm.model.poi

import play.api.libs.json.{Format, Json}

case class Payment(name: String, langId: Short)

object Payment {
  implicit val format: Format[Payment] = Json.format[Payment]
}