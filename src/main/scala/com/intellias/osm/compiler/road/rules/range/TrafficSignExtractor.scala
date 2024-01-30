package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey._
import com.intellias.osm.compiler.attributes.keys.{ConditionalKey, ConditionalSeparator}
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.compiler.road.rules.values.{OsmConditional, OsmDirection}
import com.intellias.osm.model.road.{Topology, TrafficSignType}
import play.api.libs.json.Writes

import scala.util.matching.Regex

// https://wiki.openstreetmap.org/wiki/Key:traffic_sign

object TrafficSignExtractor extends RoadRulesExtractor[TrafficSignType] {

  override implicit def writes: Writes[Seq[TrafficSignType]] = Writes.seq
  override def tag                                           = "NDS:TrafficSign"

  private val KeyPattern = keyPattern("traffic_sign", optional(OsmDirection), optional(OsmConditional))

  override def decodeFromOsm(topology: Topology): Seq[TrafficSignType] = {

    topology.tags.flatMap {
      case (key, value) =>
        KeyPattern
          .findFirstMatchIn(key)
          .map(rm => createTrafficSignType(rm, value))
    }.toSeq
  }

  private def createTrafficSignType(rm: Regex.Match, value: String): TrafficSignType = {
    val directionType = OsmDirection(rm)
    val coniditonal   = ConditionalKey(rm)

    val (finalValue, conditional) = if (coniditonal) {
      val splittedValue = value.split(ConditionalSeparator)
      (splittedValue.head, Option(splittedValue(1)))
    } else {
      (value, Option.empty)
    }

    TrafficSignType(directionType.direction, finalValue, conditional)
  }
}
