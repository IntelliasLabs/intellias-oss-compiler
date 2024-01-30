package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.keyPattern
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.model.road.{Season, Topology}
import play.api.libs.json.Writes

//https://wiki.openstreetmap.org/wiki/Key:seasonal
object SeasonalExtractor extends RoadRulesExtractor[Season] {

  override implicit def writes: Writes[Seq[Season]] = Writes.seq
  override def tag                                  = "NDS:Seasonal"

  private val KeyPattern = keyPattern("seasonal")

  override def decodeFromOsm(topology: Topology): Seq[Season] = {
    topology.tags.flatMap {
      case (key, rawValue) =>
        if (KeyPattern.findFirstMatchIn(key).nonEmpty) {
          Some(rawValue)
        } else
          None
    }.flatMap(_.split(";"))
      .flatMap(_.split(","))
      .map(_.trim)
      .filter(!_.equals("no")) //filter cases with seasonal:no
      .map(Season.toSeason)
      .toSeq
  }

}
