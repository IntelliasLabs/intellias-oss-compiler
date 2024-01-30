package com.intellias.osm.ndslive.road.rules.range

import com.intellias.osm.model.road.VehicleType
import nds.core.attributemap.Condition
import nds.core.conditions._
import nds.core.vehicle.FrequentlyUsedVehicleTypes.Values._
import nds.core.vehicle.{FrequentlyUsedVehicleTypes, PublicServiceVehicleTypes, SlowRoadUserTypes}
import zserio.runtime.io.Writer
import zserio.runtime.{SizeOf, ZserioBitmask}
trait RulesVehicleTypesBuilder {
  def createVehicleTypeCondition(vehicleType: VehicleType): Seq[Condition] = {
    createVehicleTypeConditions(Seq(vehicleType))
  }

  def createVehicleTypeConditions(vehicleTypes: Seq[VehicleType]): Seq[Condition] = {
    vehicleTypes
      .map(vehicleType => transform(vehicleType))
      .groupBy(_._1)
      .toSeq
      .map { case (typeCode, seq) => (typeCode, seq.flatMap(_._2)) }
      .flatMap { case (typeCode, seq) =>
        typeCode match {
          case ConditionTypeCode.FREQUENTLY_USED_VEHICLE_TYPES =>
            createFrequentlyUsedVehicleType(seq)
          case ConditionTypeCode.SLOW_ROAD_USERS =>
            createSlowRoadUserType(seq)
          case ConditionTypeCode.PUBLIC_SERVICE_VEHICLES =>
            createPublicServiceVehicleType(seq)
          case _ => None
        }
      }
      .map(value => new Condition(value.getCode, value))
  }

  private def createPublicServiceVehicleType(seq: Seq[Writer with SizeOf with ZserioBitmask]) = {
    val condition = new ConditionValue(ConditionTypeCode.PUBLIC_SERVICE_VEHICLES)
    condition.setPublicServiceVehicles(
      new PublicServiceVehiclesCondition(
        seq
          .asInstanceOf[Seq[PublicServiceVehicleTypes]]
          .reduceLeft(_ and _),
        true
      )
    )
    Some(condition)
  }

  private def createSlowRoadUserType(seq: Seq[Writer with SizeOf with ZserioBitmask]) = {
    val condition = new ConditionValue(ConditionTypeCode.SLOW_ROAD_USERS)
    condition.setSlowRoadUsers(
      new SlowRoadUsersCondition(
        seq
          .asInstanceOf[Seq[SlowRoadUserTypes]]
          .reduceLeft(_ and _),
        true
      )
    )
    Some(condition)
  }

  private def createFrequentlyUsedVehicleType(seq: Seq[Writer with SizeOf with ZserioBitmask]) = {
    val condition = new ConditionValue(ConditionTypeCode.FREQUENTLY_USED_VEHICLE_TYPES)
    condition.setFrequentlyUsedVehicles(
      new FrequentlyUsedVehicleTypesCondition(
        seq
          .asInstanceOf[Seq[FrequentlyUsedVehicleTypes]]
          .reduceLeft(_ and _),
        true
      )
    )
    Some(condition)
  }

  private def transform(vehicleType: VehicleType) = {
    vehicleType match {
      case VehicleType.Vehicle | VehicleType.MotorVehicle =>
        (ConditionTypeCode.FREQUENTLY_USED_VEHICLE_TYPES, Set(MOTORIZED_VEHICLE))
      case VehicleType.Motorcar =>
        (ConditionTypeCode.FREQUENTLY_USED_VEHICLE_TYPES, Set(PERSONAL_CAR))
      case VehicleType.Bus =>
        (ConditionTypeCode.FREQUENTLY_USED_VEHICLE_TYPES, Set(BUS))
      case VehicleType.PSV =>
        (ConditionTypeCode.PUBLIC_SERVICE_VEHICLES,
         Set(
           PublicServiceVehicleTypes.Values.PUBLIC_BUS,
           PublicServiceVehicleTypes.Values.SCHOOL_BUS,
           PublicServiceVehicleTypes.Values.TAXI,
           PublicServiceVehicleTypes.Values.TROLLEY,
           PublicServiceVehicleTypes.Values.MAIL,
           PublicServiceVehicleTypes.Values.EMERGENCY
         ))
      case VehicleType.Taxi =>
        (ConditionTypeCode.PUBLIC_SERVICE_VEHICLES, Set(PublicServiceVehicleTypes.Values.TAXI))
      case VehicleType.Goods =>
        (ConditionTypeCode.FREQUENTLY_USED_VEHICLE_TYPES, Set(COMMERCIAL_VEHICLE))
      case VehicleType.HGV =>
        (ConditionTypeCode.FREQUENTLY_USED_VEHICLE_TYPES, Set(TRUCK))
      case VehicleType.Motorcycle =>
        (ConditionTypeCode.FREQUENTLY_USED_VEHICLE_TYPES, Set(MOTORCYCLE))
      case VehicleType.Foot =>
        (ConditionTypeCode.SLOW_ROAD_USERS,
         Set(
           SlowRoadUserTypes.Values.PEDESTRIAN,
           SlowRoadUserTypes.Values.PEDESTRIAN_WITH_HANDCART
         ))
      case VehicleType.Bicycle =>
        (ConditionTypeCode.SLOW_ROAD_USERS,
         Set(
           SlowRoadUserTypes.Values.BICYCLE
         ))
    }
  }
}
