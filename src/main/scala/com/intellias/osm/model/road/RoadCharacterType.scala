package com.intellias.osm.model.road

import com.intellias.osm.model.JsonFormatter

trait RoadCharacterType

object RoadCharacterType extends JsonFormatter[RoadCharacterType] {
  override val values: Seq[RoadCharacterType] = Seq(
    Urban,
    ServiceArea,
    Parking,
    Covered,
    Motorway,
    Expressway,
    Ferry,
    Tunnel,
    Bridge,
    MultiDigitized,
    ComplexIntersection,
    IsElevatedRoad,
    Overpass,
    Underpass,
    RaceTrack,
    InsideCityLimits,
    PedestrianZone,
    ControlledAccess,
    PhysicallySeparated,
    TracksOnRoad,
    BicyclePath,
    BusRoad,
    HorseWay,
    TaxiRoad,
    EmergencyRoad,
    TruckEscapeRamp,
    ExpressRoad,
    StationPlaza,
    HasShoulderLane,
    TollRoad,
    MainRoad,
    UTurnRoad,
  )

  case object Urban               extends RoadCharacterType
  case object ServiceArea         extends RoadCharacterType
  case object Parking             extends RoadCharacterType
  case object Covered             extends RoadCharacterType
  case object Motorway            extends RoadCharacterType
  case object Expressway          extends RoadCharacterType
  case object Ferry               extends RoadCharacterType
  case object Tunnel              extends RoadCharacterType
  case object Bridge              extends RoadCharacterType
  case object MultiDigitized      extends RoadCharacterType
  case object ComplexIntersection extends RoadCharacterType
  case object IsElevatedRoad      extends RoadCharacterType
  case object Overpass            extends RoadCharacterType
  case object Underpass           extends RoadCharacterType
  case object RaceTrack           extends RoadCharacterType
  case object InsideCityLimits    extends RoadCharacterType
  case object PedestrianZone      extends RoadCharacterType
  case object ControlledAccess    extends RoadCharacterType
  case object PhysicallySeparated extends RoadCharacterType
  case object TracksOnRoad        extends RoadCharacterType
  case object BicyclePath         extends RoadCharacterType
  case object BusRoad             extends RoadCharacterType
  case object HorseWay            extends RoadCharacterType
  case object TaxiRoad            extends RoadCharacterType
  case object EmergencyRoad       extends RoadCharacterType
  case object TruckEscapeRamp     extends RoadCharacterType
  case object ExpressRoad         extends RoadCharacterType
  case object StationPlaza        extends RoadCharacterType
  case object HasShoulderLane     extends RoadCharacterType
  case object TollRoad            extends RoadCharacterType
  case object MainRoad            extends RoadCharacterType
  case object UTurnRoad           extends RoadCharacterType
}
