package com.intellias.osm.model.poi

import play.api.libs.json.{Format, Json}

case class PaymentName(name: String, langIsoCode: String)

object PaymentName {
  implicit val format: Format[PaymentName] = Json.format[PaymentName]
}