package com.intellias.osm.model.road

import com.intellias.osm.model.JsonFormatter

trait VehicleType {}

object VehicleType extends JsonFormatter[VehicleType] {
  val values: Seq[VehicleType] = Seq(
    Vehicle,
    MotorVehicle,
    Motorcar,
    Bus,
    PSV,
    Taxi,
    Goods,
    HGV,
    Foot,
    Bicycle,
    Motorcycle
  )

  final case object Vehicle extends VehicleType

  final case object MotorVehicle extends VehicleType

  final case object Motorcar extends VehicleType

  final case object Bus extends VehicleType

  final case object PSV extends VehicleType

  final case object Taxi extends VehicleType

  final case object Goods extends VehicleType

  final case object HGV extends VehicleType

  final case object Foot extends VehicleType

  final case object Bicycle extends VehicleType

  final case object Motorcycle extends VehicleType

}
