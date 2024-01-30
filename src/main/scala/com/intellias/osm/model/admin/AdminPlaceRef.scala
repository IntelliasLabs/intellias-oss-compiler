package com.intellias.osm.model.admin

import play.api.libs.json.{Format, Json}

case class AdminPlaceRef(adminPlaceId: String, adminLevel: Int)

object AdminPlaceRef {
  implicit val adminPlaceRefFormat: Format[AdminPlaceRef] = Json.format[AdminPlaceRef]
}