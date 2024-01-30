package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey._
import com.intellias.osm.compiler.attributes.keys.{ConditionalKey, ConditionalSeparator}
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.compiler.road.rules.range.ModelMapper.{toDirection, toVehicleType}
import com.intellias.osm.compiler.road.rules.values.{OsmConditional, OsmDirection, OsmVehicleType}
import com.intellias.osm.model.road
import com.intellias.osm.model.road.{DeniedAccess, Topology, VehicleType}
import play.api.libs.json.Writes

import scala.util.matching.Regex

// https://wiki.openstreetmap.org/wiki/Key:access

object AccessExtractor extends RoadRulesExtractor[DeniedAccess] {
  override implicit def writes: Writes[Seq[DeniedAccess]] = Writes.seq

  override def tag = "NDS:ProhibitedPassage"

  private val KeyPattern = keyPattern(
    "access",
    optional(OsmVehicleType),
    optional(OsmDirection),
    optional(OsmConditional)
  )

  override def decodeFromOsm(topology: Topology): Seq[DeniedAccess] =
    topology.tags.flatMap {
      case (key, rawValue) =>
        KeyPattern
          .findFirstMatchIn(key)
          .flatMap(createOsmAccess(rawValue, topology.tags, _))
    }.toSeq

  private def createOsmAccess(rawValue: String, tags: Map[String, String], regexMatch: Regex.Match) = Option[DeniedAccess] {
    val (value, condition) = if (ConditionalKey(regexMatch)) {
      val strings = rawValue.split(ConditionalSeparator)
      (strings.head, Some(strings(1).trim))
    } else {
      (rawValue, None)
    }

    val list = OsmVehicleType.values.map(value => value.value).toSet

    val vehicleList: Seq[VehicleType] = {
      if (list.contains(value)) {

        (OsmVehicleType.values.toSet -- Seq(OsmVehicleType.getVehicleTypeByValue(value))).toSeq
      } else {
        value match {
          case "yes" =>
            tags
              .filter(pair => list.contains(pair._1) && pair._2 == "no")
              .map(pair => OsmVehicleType.getVehicleTypeByValue(pair._1))
              .toSeq
          case "no" | "private" =>
            val seq = tags
              .filter(pair => list.contains(pair._1) && pair._2 == "yes")
              .map(pair => OsmVehicleType.getVehicleTypeByValue(pair._1))
              .toSeq
            if (seq.isEmpty) {
              seq
            } else {
              (OsmVehicleType.values.toSet -- seq).toSeq
            }
          case _ => Seq.empty[OsmVehicleType]

        }
      }
    }.flatMap(osmtype => toVehicleType(osmtype))

    road.DeniedAccess(
      vehicleList,
      toDirection(OsmDirection(regexMatch)),
      condition
    )

  }
}
