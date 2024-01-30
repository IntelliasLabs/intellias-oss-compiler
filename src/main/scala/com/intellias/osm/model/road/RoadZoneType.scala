package com.intellias.osm.model.road

import com.intellias.osm.model.JsonFormatter

trait RoadZoneType {}
object RoadZoneType extends JsonFormatter[RoadZoneType] {
  val values: Seq[RoadZoneType] = Seq(
    UrbanZone,
    RuralZone,
    MotorwayZone,
    Undefined
  )

  case object UrbanZone    extends RoadZoneType
  case object RuralZone    extends RoadZoneType
  case object MotorwayZone extends RoadZoneType
  case object Undefined    extends RoadZoneType

  def toRoadZone(string: String): RoadZoneType = {
    string.toLowerCase match {
      case "urban"                => UrbanZone
      case "rural"                => RuralZone
      case "motorway" | "highway" => MotorwayZone
      case _                      => Undefined
    }
  }
}
