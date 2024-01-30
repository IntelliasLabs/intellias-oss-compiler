package com.intellias.osm.model.road

import com.intellias.osm.model.JsonFormatter

trait RoadFormType

object RoadFormType extends JsonFormatter[RoadFormType] {
  override val values: Seq[RoadFormType] = Seq(
    Roundabout,
    Ramp,
    DualCarriageway,
    SlipRoad,
    PedestrianWay,
    ServiceRoad,
    Any,
    Normal
  )

  case object Roundabout extends RoadFormType
  case object Ramp extends RoadFormType
  case object DualCarriageway extends RoadFormType
  case object SlipRoad extends RoadFormType
  case object PedestrianWay extends RoadFormType
  case object ServiceRoad extends RoadFormType
  case object Any extends RoadFormType
  case object Normal extends RoadFormType
}
