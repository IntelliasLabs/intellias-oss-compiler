package com.intellias.osm.model.poi

import play.api.libs.json.{Format, Json}

case class EvChargingConnector(socketType: SocketType, socketPower: Int, voltage: Short, countSockets: Short)

object EvChargingConnector {
  implicit val format: Format[EvChargingConnector] = Json.format[EvChargingConnector]
}