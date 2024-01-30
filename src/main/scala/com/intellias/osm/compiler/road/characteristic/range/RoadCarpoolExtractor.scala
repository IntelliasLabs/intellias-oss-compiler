package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.compiler.condition.OsmOpeningHoursToCondition
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.model.condition.TimeCondition
import com.intellias.osm.model.road.{Carpool, CarpoolType, Topology}
import play.api.libs.json.Writes

object RoadCarpoolExtractor extends RoadCharacteristicsExtractor[Carpool] with Serializable {
  override val tag: String                           = "NDS:Carpoool"
  override implicit def writes: Writes[Seq[Carpool]] = Writes.seq

  override def decodeFromOsm(feature: Topology): Seq[Carpool] = {
    val carpoolType = feature match {
      case road if isDesignatedCarpoolRoad(road) => Some(CarpoolType.Complete)
      case road if isPartialCarpool(road)        => Some(CarpoolType.Partial)
      case _                                     => None
    }

    carpoolType.map { carpoolType =>
      val occupancy     = feature.tags.get("hov:minimum").flatMap(n => n.toIntOption).getOrElse(2)
      val hourCondition = hovHoursCondition(feature)
      Seq(Carpool(carpoolType, timeConditions = hourCondition, occupancy = occupancy))
    }.getOrElse(Seq.empty)
  }

  private def isDesignatedCarpoolRoad(topology: Topology): Boolean = topology.tagValue("hov", "designated")
  private def isPartialCarpool(topo: Topology): Boolean =
    topo.oneOfRegex("hov", "lane") ||
      topo.oneOfRegex("hov:lanes", ".*designated.*") || topo.tag("hov:minimum")

  private def hovHoursCondition(topology: Topology): Seq[TimeCondition] = {
    topology.tags
      .get("hov:conditional")
      .orElse(topology.tags.get("hov:lanes:conditional"))
      .map(hovOpeningHoursTo)
      .getOrElse(List.empty)
      .toSeq
  }

  private def hovOpeningHoursTo(str: String): Iterable[TimeCondition] = {
    val Pattern = ".*@ \\((.*)\\).*".r
    str match {
      case Pattern(group) => OsmOpeningHoursToCondition.buildCondition(group).getOrElse(List.empty)
      case _              => List.empty
    }
  }
}
