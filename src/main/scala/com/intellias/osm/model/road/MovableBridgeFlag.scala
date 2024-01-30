package com.intellias.osm.model.road

import play.api.libs.json.{Format, Json}

case class MovableBridgeFlag(isMovableBridge: Boolean = true)

object MovableBridgeFlag {
  implicit val format: Format[MovableBridgeFlag] = Json.format[MovableBridgeFlag]
}
