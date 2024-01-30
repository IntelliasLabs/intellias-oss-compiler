package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.{ConditionalKey, ConditionalSeparator}
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey._
import com.intellias.osm.compiler.road.rules.range.ModelMapper.{toDirection, toVehicleType}
import com.intellias.osm.compiler.road.rules.values.{OsmConditional, OsmDirection, OsmVehicleType}
import com.intellias.osm.model.road.{SpeedLimit, SpeedLimitType, Topology}
import play.api.libs.json.{Format, Json, Writes}

import scala.util.matching.Regex

trait SpeedExtractor extends RoadRulesExtractor[SpeedLimit] {
  override implicit def writes: Writes[Seq[SpeedLimit]] = Writes.seq

  implicit val format: Format[SpeedLimit] = Json.format[SpeedLimit]
  def mainOsmAttr: String
  def speedLimitType: SpeedLimitType

  private val KeyPattern = keyPattern(
    mainOsmAttr,
    optional(OsmVehicleType),
    optional(OsmDirection),
    optional(OsmConditional)
  )

  private val KPH = speedLimitValueRegex(Set("", "km/h", "kmh", "kph"))
  private val MPH = speedLimitValueRegex(Set("mph"))

  private def speedLimitValueRegex(units: Set[String]) =
    (
      "^(?<zone>[a-z]{2}:(?:[a-z]+:)*)?(?<value>[0-9]+)[ \t]*(?:" + units.mkString("|") + ")" + "$"
    ).r

  override def decodeFromOsm(topology: Topology): Seq[SpeedLimit] =
    topology.tags.flatMap {
      case (key, rawValue) =>
        KeyPattern
          .findFirstMatchIn(key)
          .flatMap(createSpeedLimitValue(rawValue, _))
    }.toSeq

  private def createSpeedLimitValue(rawValue: String, regexMatch: Regex.Match) = {
    val (value, condition) = if (ConditionalKey(regexMatch)) {
      rawValue.split(ConditionalSeparator).toList match {
        case value :: conditional :: Nil => (value, Some(conditional))
        case _                           => (rawValue, None)
      }
    } else {
      (rawValue, None)
    }

    (value match {
      case KPH(_, value) => Option((value, true))
      case MPH(_, value) => Option((value, false))
      case _             => None
    }).map {
      case (value, isMetric) =>
        SpeedLimit(value.toInt, isMetric, toVehicleType(OsmVehicleType(regexMatch)), toDirection(OsmDirection(regexMatch)), speedLimitType, condition)
    }
  }
}

// https://wiki.openstreetmap.org/wiki/Key:maxspeed
// https://wiki.openstreetmap.org/wiki/Key:maxspeed:conditional
// TODO: https://wiki.openstreetmap.org/wiki/Key:maxspeed:variable
object MaxSpeedExtractor extends SpeedExtractor {
  override def tag = "NDS:MaxSpeed"

  override def mainOsmAttr: String = "maxspeed"

  override def speedLimitType: SpeedLimitType = SpeedLimitType.MaxSpeed

}
// https://wiki.openstreetmap.org/wiki/Key:minspeed
object MinSpeedExtractor extends SpeedExtractor {
  override def tag = "NDS:MinSpeed"

  override def mainOsmAttr: String = "minspeed"

  override def speedLimitType: SpeedLimitType = SpeedLimitType.MinSpeed
}

// https://wiki.openstreetmap.org/wiki/Key:maxspeed:advisory
object AdvisorySpeedExtractor extends SpeedExtractor {
  override def tag = "NDS:AdvisorySpeed"

  override def mainOsmAttr: String = "maxspeed:advisory"

  override def speedLimitType: SpeedLimitType = SpeedLimitType.AdvisorySpeed
}
