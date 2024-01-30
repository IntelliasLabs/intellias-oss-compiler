package com.intellias.osm.model.road

import com.intellias.osm.model.common.DirectionType

case class SpeedLimit(value: Int,
                      isMetric: Boolean,
                      vehicle: Option[VehicleType],
                      direction: DirectionType,
                      limitType: SpeedLimitType,
                      condition: Option[String]) {

}
