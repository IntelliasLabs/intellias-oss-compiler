package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.{flag, keyPattern, optional}
import com.intellias.osm.compiler.attributes.keys.{ConditionalKey, ConditionalSeparator, OsmRoadSide}
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.compiler.road.rules.values.OsmConditional
import com.intellias.osm.model.road.{ParkingType, Topology}
import play.api.libs.json.{Format, Json, Writes}

import scala.util.matching.Regex

//https://wiki.openstreetmap.org/wiki/Street_parking
object ParkingExtractor extends RoadRulesExtractor[ParkingType] {
  override implicit def writes: Writes[Seq[ParkingType]] = Writes.seq
  implicit val format: Format[ParkingType]               = Json.format[ParkingType]
  private val restriction                                = "restriction"
  private val KeyPattern                                 = keyPattern("parking", optional(OsmRoadSide), optional(flag(restriction)), optional(OsmConditional))
  override def tag                                       = "NDS:Parking"

  override def decodeFromOsm(topology: Topology): Seq[ParkingType] = {
    topology.tags.flatMap {
      case (key, value) =>
        KeyPattern
          .findFirstMatchIn(key)
          .map(rm => createParkingType(rm, value))
    }.toSeq
  }

  private def createParkingType(rm: Regex.Match, value: String): ParkingType = {
    val roadside        = OsmRoadSide(rm)
    val restrictionFlag = flag(restriction)(rm)
    val coniditonal     = ConditionalKey(rm)

    val (finalValue, conditional) = if (coniditonal) {
      val splittedValue = value.split(ConditionalSeparator)
      (splittedValue.head, Option(splittedValue(1)))
    } else {
      (value, Option.empty)
    }
    val restrOptional = if (restrictionFlag) {
      Option(restriction)
    } else {
      Option.empty
    }

    ParkingType(roadside.side, restrOptional, finalValue, conditional)
  }
}
