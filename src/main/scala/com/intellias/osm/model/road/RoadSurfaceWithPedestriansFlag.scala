package com.intellias.osm.model.road

import play.api.libs.json.{Format, Json}

case class RoadSurfaceWithPedestriansFlag(isMovableBridge: Boolean = true)

object RoadSurfaceWithPedestriansFlag {
  implicit val format: Format[RoadSurfaceWithPedestriansFlag] = Json.format[RoadSurfaceWithPedestriansFlag]
}
