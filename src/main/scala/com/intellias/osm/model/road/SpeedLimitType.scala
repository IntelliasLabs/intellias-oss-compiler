package com.intellias.osm.model.road

import com.intellias.osm.model.JsonFormatter

trait SpeedLimitType {}
object SpeedLimitType extends JsonFormatter[SpeedLimitType] {
  val values: Seq[SpeedLimitType] = Seq(
    MinSpeed,
    MaxSpeed,
    AdvisorySpeed
  )

  case object MinSpeed      extends SpeedLimitType
  case object MaxSpeed      extends SpeedLimitType
  case object AdvisorySpeed extends SpeedLimitType

}
