package com.intellias.osm.model.road

import play.api.libs.json.{Format, Json}

case class StartOrDestinationRoadFlag(isMovableBridge: Boolean = true)

object StartOrDestinationRoadFlag {
  implicit val format: Format[StartOrDestinationRoadFlag] = Json.format[StartOrDestinationRoadFlag]
}
