package com.intellias.osm.compiler.road.rules.values

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey

// https://wiki.openstreetmap.org/wiki/Conditional_restrictions#Transportation_mode
// https://wiki.openstreetmap.org/wiki/Key:access#Transport_mode_restrictions
sealed abstract class OsmVehicleType(val value: String) {
  override def toString: String = value
}

case object OsmVehicleType extends OsmCompositeKey[OsmVehicleType] {
  override val values: Seq[OsmVehicleType] = Seq(
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

  override val extraValuesForReaders: Seq[OsmVehicleType] = Seq(All)

  override def groupName: String = "vehicleType"

  override def default: OsmVehicleType = All

  final case object All extends OsmVehicleType("")

  final case object Vehicle extends OsmVehicleType("vehicle")

  final case object MotorVehicle extends OsmVehicleType("motor_vehicle")

  final case object Motorcar extends OsmVehicleType("motorcar")

  final case object Bus extends OsmVehicleType("bus")

  final case object PSV extends OsmVehicleType("psv")

  final case object Taxi extends OsmVehicleType("taxi")

  final case object Goods extends OsmVehicleType("goods")

  final case object HGV extends OsmVehicleType("hgv")

  final case object Foot extends OsmVehicleType("foot")

  final case object Bicycle extends OsmVehicleType("bicycle")

  final case object Motorcycle extends OsmVehicleType("motorcycle")

  def getVehicleTypeByValue(value:String): OsmVehicleType = {
    value match {
      case "vehicle" => Vehicle
      case "motor_vehicle" => MotorVehicle
      case "motorcar" => Motorcar
      case "bus" => Bus
      case "psv" => PSV
      case "taxi" => Taxi
      case "goods" => Goods
      case "hgv" => HGV
      case "foot" => Foot
      case "bicycle" => Bicycle
      case "motorcycle" => Motorcycle
      case _ => throw new IllegalArgumentException(s"Vehicle of type $value does not expected")
    }

  }
}