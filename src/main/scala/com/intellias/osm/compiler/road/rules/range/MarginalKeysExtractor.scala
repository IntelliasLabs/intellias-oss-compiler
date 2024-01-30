package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.keyPattern
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.model.road.{MarginalKeyType, Topology}
import play.api.libs.json.Writes

//These keys has no relevant OSM wiki page
object MarginalKeysExtractor extends RoadRulesExtractor[MarginalKeyType] {

  override implicit def writes: Writes[Seq[MarginalKeyType]] = Writes.seq
  override def tag                                           = "NDS:marginal"

  private val KeyPattern = keyPattern("(stopping|traffic_restriction)?")

  override def decodeFromOsm(topology: Topology): Seq[MarginalKeyType] = {
    topology.tags.flatMap {
      case (key, rawValue) =>
        if (KeyPattern.findFirstMatchIn(key).nonEmpty) {
          Some(MarginalKeyType(key, rawValue, Option.empty))
        } else {
          None
        }
    }.toSeq
    Seq.empty
  }

}
