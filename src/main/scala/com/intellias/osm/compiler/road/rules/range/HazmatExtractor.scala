package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey._
import com.intellias.osm.compiler.attributes.keys.{ConditionalKey, ConditionalSeparator}
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.compiler.road.rules.values._
import com.intellias.osm.model.road.{Hazmat, Topology}
import play.api.libs.json.{Format, Json, Writes}

import scala.util.matching.Regex

// https://wiki.openstreetmap.org/wiki/Key:hazmat
object HazmatExtractor extends RoadRulesExtractor[Hazmat] {

  implicit val format: Format[Hazmat]      = Json.format[Hazmat]
  implicit def writes: Writes[Seq[Hazmat]] = Writes.seq

  private val KeyPattern = keyPattern(
    "hazmat",
    optional(OsmHazmatType),
    optional(OsmDirection),
    optional(OsmConditional)
  )

  override def tag = "NDS:Hazmat"

  private def createHazmat(rm: Regex.Match, value: String): Hazmat = {
    val osmHazmatType = OsmHazmatType(rm)
    val direction     = OsmDirection(rm)
    val coniditonal   = ConditionalKey(rm)

    val (finalValue, conditional) = if (coniditonal) {
      val splittedValue = value.split(ConditionalSeparator)
      (splittedValue.head, Option(splittedValue(1)))
    } else {
      (value, Option.empty)
    }

    Hazmat(finalValue, osmHazmatType.hazmatType, conditional, direction.direction)
  }

  override def decodeFromOsm(topology: Topology): Seq[Hazmat] = {
    topology.tags.flatMap {
      case (key, value) =>
        KeyPattern
          .findFirstMatchIn(key)
          .map(rm => createHazmat(rm, value))
    }.toSeq
  }
}
