package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.road.highway
import com.intellias.osm.model.common.FeatureRange
import com.intellias.osm.model.road.{RoadCharacterType, RoadCharacteristics, Topology}
import play.api.libs.json.{Json, Writes}

object RoadTypeCharsExtractor extends RoadCharacteristicsExtractor[RoadCharacteristics] {
  override val tag: String                                       = "NDS:RoadCharacter"
  override implicit def writes: Writes[Seq[RoadCharacteristics]] = Writes.seq


  private val charChecker: Map[RoadCharacterType, Topology => Boolean] = Map(
    RoadCharacterType.Parking             -> isParking,
    RoadCharacterType.Motorway            -> isMotorway,
    RoadCharacterType.Tunnel              -> isTunnel,
    RoadCharacterType.Bridge              -> isBridge,
    RoadCharacterType.RaceTrack           -> isRace,
    RoadCharacterType.TracksOnRoad        -> isTracked,
    RoadCharacterType.BusRoad             -> isBusOnly,
    RoadCharacterType.TollRoad            -> isTollRoad,
    RoadCharacterType.Covered             -> isCovered, //https://wiki.openstreetmap.org/wiki/Key:covered
    RoadCharacterType.ServiceArea         -> isServiceArea,
    RoadCharacterType.EmergencyRoad       -> isEmergencyOnly, //https://wiki.openstreetmap.org/wiki/Tag:service%3Demergency_access
    RoadCharacterType.ExpressRoad         -> isExpressRoad,
    RoadCharacterType.Expressway          -> isExpressWay,
    RoadCharacterType.Ferry               -> isFerry,
    RoadCharacterType.RaceTrack           -> isRaceTrack,
    RoadCharacterType.BicyclePath         -> isBicyclePath,
    RoadCharacterType.TaxiRoad            -> isTaxiRoad,
    RoadCharacterType.Overpass            -> isOverPass,
    RoadCharacterType.Underpass           -> isUnderPass,
    RoadCharacterType.PedestrianZone      -> isPedestrianZone,
    RoadCharacterType.HorseWay            -> isHorseWay,
    RoadCharacterType.ControlledAccess    -> isControlledAccess,
    RoadCharacterType.MultiDigitized      -> isMultiDigitized,
    RoadCharacterType.PhysicallySeparated -> isPhysicallySeparated,
    RoadCharacterType.Urban               -> isUrban,
    RoadCharacterType.InsideCityLimits    -> isUrban,
    RoadCharacterType.HasShoulderLane     -> hasShoulderLane,
    RoadCharacterType.TruckEscapeRamp     -> isTruckEscapeRamp
  )

  override def decodeFromOsm(feature: Topology): Seq[RoadCharacteristics] = {
    val newCharacteristics     = detectRoadChars(feature)
    val existedCharacteristics = getExistChars(feature)

    mergeCharacteristicsByRange(newCharacteristics, existedCharacteristics)
  }

  def mergeCharacteristicsByRange(newCharacteristics: Seq[(FeatureRange, Seq[RoadCharacterType])],
                                  existedCharacteristics: Seq[(FeatureRange, Seq[RoadCharacterType])]): Seq[RoadCharacteristics] = {
    (newCharacteristics ++ existedCharacteristics)
      .groupMap(_._1){ case (_, charTypes) => charTypes }
      .map {
        case (range, chars) =>
          RoadCharacteristics(chars.flatten, range)
      }
      .toSeq
  }

  def detectRoadChars(feature: Topology): Seq[(FeatureRange, Seq[RoadCharacterType])] = Seq {
    val chars = charChecker.collect {
      case (characterType, fn) if fn(feature) => characterType
    }.toSeq

    (FeatureRange.Complete, chars)
  }

  def getExistChars(feature: Topology): Seq[(FeatureRange, Seq[RoadCharacterType])] = {
    feature.tags
      .get(tag)
      .map { json =>
        Json.parse(json).as[Seq[RoadCharacteristics]].map(rangeAndChar => rangeAndChar.range -> rangeAndChar.characterTypes)
      }
      .getOrElse(Seq.empty)
  }

  private def isParking(topo: Topology): Boolean  = topo.allOff(highway -> "service", "service" -> "parking_aisle")
  private def isMotorway(topo: Topology): Boolean = topo.oneOf(highway, Set("motorway", "motorway_link"))
  private def isTunnel(topo: Topology): Boolean   = topo.tag("tunnel")
  private def isBridge(topo: Topology): Boolean   = topo.tag("bridge")
  private def isRace(topo: Topology): Boolean     = topo.tagValue(highway, "raceway")
  private def isTracked(topo: Topology): Boolean  = topo.tagValue("railway", "tram")
  private def isBusOnly(topo: Topology): Boolean =
    topo.tagValue(highway, "busway") || (topo.tagValue(highway, "service") && topo.tagValue("bus", "designated"))
  private def isTollRoad(nr: Topology): Boolean      = nr.tag(highway) && nr.tagValue("toll", "yes")
  private def isServiceArea(nr: Topology): Boolean   = nr.tagValue(highway, "service")
  private def isCovered(nr: Topology): Boolean       = nr.tag("covered") && !nr.tagValue("covered", "no")
  private def isEmergencyOnly(nr: Topology): Boolean = nr.tagValue("service", "emergency_access")
  private def isExpressWay(nr: Topology): Boolean    = nr.tagValue("expressway", "yes") && !nr.oneOf(highway, Set("motorway_link", "motorway"))
  private def isExpressRoad(nr: Topology): Boolean   = nr.tagValue(highway, "motorway") || nr.tagValue(highway, "trunk")
  private def isFerry(nr: Topology): Boolean         = nr.tagValue("route", "ferry")
  private def isRaceTrack(nr: Topology): Boolean     = nr.tagValue(highway, "raceway")
  private def isBicyclePath(nr: Topology): Boolean =
    nr.oneOf("bicycle_road" -> "yes", "cyclestreet" -> "yes", highway -> "cycleway") ||
      (nr.tagValue(highway, "path") && nr.tagValue("bicycle", "designated"))
  private def isTaxiRoad(nr: Topology): Boolean            = nr.tagValue("taxi", "designated")
  private def isOverPass(nr: Topology): Boolean            = nr.nodes.map(_.zLevel).max > 0
  private def isUnderPass(nr: Topology): Boolean           = nr.nodes.map(_.zLevel).max < 0
  private def isPedestrianZone(nr: Topology): Boolean      = nr.tagValue(highway, "pedestrian")
  private def isHorseWay(nr: Topology): Boolean            = nr.tags.get(highway).contains("bridleway")
  private def isControlledAccess(nr: Topology): Boolean    = isMotorway(nr) || (nr.tag(highway) && nr.tagValue("motorroad", "yes"))
  private def isMultiDigitized(nr: Topology): Boolean      = nr.oneOf(highway, Set("motorway", "trunk", "primary")) && nr.tagValue("oneway", "yes")
  private def isPhysicallySeparated(nr: Topology): Boolean = nr.oneOf(highway, Set("motorway", "trunk"))
  private def isUrban(nr: Topology): Boolean =
    nr.oneOf(highway, Set("living_street", "residential", "tertiary", "service")) ||
      nr.oneOfRegex("maxspeed", ".*:urban", ".*urban") || nr.oneOfRegex("source:maxspeed", ".*urban") ||
      nr.oneOfRegex("maxspeed:type", ".*urban") || nr.oneOfRegex("zone:maxspeed", ".*urban") ||
      nr.oneOfRegex("zone:traffic", ".*urban") || nr.oneOfRegex("HFCS", ".*Urban.*")

//  private def isInsideCityLimits(nr: Topology): Boolean = nr.oneOf(highway, Set("living_street", "residential"))
  private def hasShoulderLane(nr: Topology): Boolean   = nr.oneOf("shoulder", Set("yes", "left", "right", "both"))
  private def isTruckEscapeRamp(nr: Topology): Boolean = nr.tagValue(highway, "escape")

}
