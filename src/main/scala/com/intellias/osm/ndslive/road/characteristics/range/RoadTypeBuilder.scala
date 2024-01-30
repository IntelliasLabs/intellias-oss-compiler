package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.{RoadTypeCharsExtractor, RoadTypeFormExtractor}
import com.intellias.osm.compiler.road.common.range.RoadRange
import com.intellias.osm.compiler.road.common.range.RoadRange._
import com.intellias.osm.model.common.{DirectionType, FeatureRange}
import com.intellias.osm.model.road.{NdsRoad, RoadCharacterType, RoadCharacteristics, RoadFormType}
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations.{CharacsProperty, CharacsPropertyList, CharacsRoadRangeAttributeSetMap, CharacsRoadRangeFullAttribute}
import nds.core.attributemap.{Condition, ConditionList}
import nds.core.types.{RoadCharacter, RoadForm, RoadType}
import play.api.libs.json.Json

object RoadTypeBuilder extends RangeAttributeValueBuilder with Serializable {

  override def buildAttributes(roads: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    val roadsWithForms: Seq[(NdsRoad, RoadForm, Seq[RoadCharacter], FeatureRange)] = roads.flatMap { road =>
      val roadForm = road.tags
        .get(RoadTypeFormExtractor.tag)
        .map(json => Json.parse(json).as[Seq[RoadFormType]])
        .getOrElse(Seq.empty)
        .headOption
        .map(toRoadForm)
        .getOrElse(RoadForm.ANY)

      val rangeToChars: Seq[(FeatureRange, Seq[RoadCharacter])] = road.tags
        .get(RoadTypeCharsExtractor.tag)
        .map { json =>
          Json
            .parse(json)
            .as[Seq[RoadCharacteristics]]
            .map(rChars => (rChars.range, rChars.characterTypes.map(toRoadCharacter)))
        }
        .getOrElse(Seq.empty)
        .groupMap { case (range, _) => range } { case (_, chars) => chars }
        .map { case (range, chars) => (range, chars.flatten) }
        .toSeq

      rangeToChars.map {
        case (range, chars) =>
          (road, roadForm, chars, range)
      }
    }.toList

    val roadTypeAttributes: Iterable[CharacsRoadRangeAttributeSetMap] = roadsWithForms.groupMap {
      case (_, form, chars, range) => (form, chars, range)
    } { case (road, _, _, _) => road }.map {
      case ((form, chars, range), roads) =>
        val refs = roads.map(road => (road, DirectionType.Both, range.toRoadRange))
        build(form, chars, refs)
    }

    roadTypeAttributes
  }

  def build(roadForm: RoadForm, chars: Seq[RoadCharacter], refs: Iterable[(NdsRoad, DirectionType, RoadRange)]): CharacsRoadRangeAttributeSetMap = {
    val roadType = new RoadType(roadForm, chars.toArray)

    val roadTypeAttrValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.ROAD_TYPE)
    roadTypeAttrValue.setRoadType(roadType)

    // encapsulates attribute type code and certain value of this attribute with optional set of properties and conditions
    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.ROAD_TYPE,
      roadTypeAttrValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]), // in documentation is marked as optional which means that `null` value can be used here
      new ConditionList(0.toShort, Array.empty[Condition]) // in documentation is marked as optional which means that `null` value can be used here
    )

    buildRoadRangeAttributeSetMap(refs, roadTypeFullAttribute)
  }

  def toRoadForm(formType: RoadFormType): RoadForm = {
    formType match {
      case RoadFormType.Roundabout      => RoadForm.ROUNDABOUT
      case RoadFormType.Ramp            => RoadForm.RAMP
      case RoadFormType.DualCarriageway => RoadForm.DUAL_CARRIAGEWAY
      case RoadFormType.SlipRoad        => RoadForm.SLIP_ROAD
      case RoadFormType.PedestrianWay   => RoadForm.PEDESTRIAN_WAY
      case RoadFormType.ServiceRoad     => RoadForm.SERVICE_ROAD
      case RoadFormType.Any             => RoadForm.ANY
      case RoadFormType.Normal          => RoadForm.NORMAL
    }
  }

  def toRoadCharacter(roadChar: RoadCharacterType): RoadCharacter = roadChar match {
    case RoadCharacterType.Parking             => RoadCharacter.PARKING
    case RoadCharacterType.Motorway            => RoadCharacter.MOTORWAY
    case RoadCharacterType.Tunnel              => RoadCharacter.TUNNEL
    case RoadCharacterType.Bridge              => RoadCharacter.BRIDGE
    case RoadCharacterType.RaceTrack           => RoadCharacter.RACE_TRACK
    case RoadCharacterType.TracksOnRoad        => RoadCharacter.TRACKS_ON_ROAD
    case RoadCharacterType.BusRoad             => RoadCharacter.BUS_ROAD
    case RoadCharacterType.TollRoad            => RoadCharacter.TOLL_ROAD
    case RoadCharacterType.Covered             => RoadCharacter.COVERED
    case RoadCharacterType.ServiceArea         => RoadCharacter.SERVICE_AREA
    case RoadCharacterType.EmergencyRoad       => RoadCharacter.EMERGENCY_ROAD
    case RoadCharacterType.ExpressRoad         => RoadCharacter.EXPRESS_ROAD
    case RoadCharacterType.Expressway          => RoadCharacter.EXPRESSWAY
    case RoadCharacterType.Ferry               => RoadCharacter.FERRY
    case RoadCharacterType.BicyclePath         => RoadCharacter.BICYCLE_PATH
    case RoadCharacterType.TaxiRoad            => RoadCharacter.TAXI_ROAD
    case RoadCharacterType.Overpass            => RoadCharacter.OVERPASS
    case RoadCharacterType.Underpass           => RoadCharacter.UNDERPASS
    case RoadCharacterType.PedestrianZone      => RoadCharacter.PEDESTRIAN_ZONE
    case RoadCharacterType.HorseWay            => RoadCharacter.HORSE_WAY
    case RoadCharacterType.ControlledAccess    => RoadCharacter.CONTROLLED_ACCESS
    case RoadCharacterType.MultiDigitized      => RoadCharacter.MULTI_DIGITIZED
    case RoadCharacterType.PhysicallySeparated => RoadCharacter.PHYSICALLY_SEPARATED
    case RoadCharacterType.Urban               => RoadCharacter.URBAN
    case RoadCharacterType.InsideCityLimits    => RoadCharacter.INSIDE_CITY_LIMITS
    case RoadCharacterType.HasShoulderLane     => RoadCharacter.HAS_SHOULDER_LANE
    case RoadCharacterType.TruckEscapeRamp     => RoadCharacter.TRUCK_ESCAPE_RAMP
  }
}
