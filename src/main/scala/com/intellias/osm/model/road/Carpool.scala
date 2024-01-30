package com.intellias.osm.model.road

import com.intellias.osm.model.condition.TimeCondition
import play.api.libs.json.{Format, Json}

case class Carpool (carpoolType: CarpoolType, timeConditions: Seq[TimeCondition], occupancy: Int)

object Carpool {
  implicit val format: Format[Carpool] = Json.format[Carpool]
}
