package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.road.rules.values.{OsmDirection, OsmVehicleType}
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.common.DirectionType.{Backward, Both, Forward}
import com.intellias.osm.model.road.VehicleType.{Bicycle, Bus, Foot, Goods, HGV, MotorVehicle, Motorcar, Motorcycle, PSV, Taxi, Vehicle}
import com.intellias.osm.model.road.VehicleType

object ModelMapper {
  def toVehicleType(osmVehicleType: OsmVehicleType): Option[VehicleType] = {
    osmVehicleType match {
      case OsmVehicleType.All          => None
      case OsmVehicleType.Vehicle      => Some(Vehicle)
      case OsmVehicleType.MotorVehicle => Some(MotorVehicle)
      case OsmVehicleType.Motorcar     => Some(Motorcar)
      case OsmVehicleType.Bus          => Some(Bus)
      case OsmVehicleType.PSV          => Some(PSV)
      case OsmVehicleType.Taxi         => Some(Taxi)
      case OsmVehicleType.Goods        => Some(Goods)
      case OsmVehicleType.HGV          => Some(HGV)
      case OsmVehicleType.Foot         => Some(Foot)
      case OsmVehicleType.Bicycle      => Some(Bicycle)
      case OsmVehicleType.Motorcycle   => Some(Motorcycle)
    }
  }

  def toDirection(osmDirection: OsmDirection): DirectionType = {
    osmDirection match {
      case OsmDirection.Both     => Both
      case OsmDirection.Forward  => Forward
      case OsmDirection.Backward => Backward
    }
  }

}
