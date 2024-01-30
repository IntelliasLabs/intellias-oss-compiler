package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.keyPattern
import com.intellias.osm.compiler.road.rules.RoadRulesExtractor
import com.intellias.osm.model.road.{Construction, Topology}
import play.api.libs.json.Writes

//https://wiki.openstreetmap.org/wiki/Key:construction
object ConstructionExtractor extends RoadRulesExtractor[Construction] {

  override implicit def writes: Writes[Seq[Construction]] = Writes.seq
  override def tag                                        = "NDS:Construction"

  private val KeyPattern = keyPattern("construction")

  override def decodeFromOsm(topology: Topology): Seq[Construction] = {
    topology.tags.flatMap {
      case (key, value) =>
        KeyPattern
          .findFirstMatchIn(key)
          .map(_ => createConstruction(value, topology.tags))
    }.toSeq
  }

  def createConstruction(value: String, tags: Map[String, String]): Construction = {
    val element = tags.toSeq
      .filter(_._2.equals("construction"))
      .map(_._1)

    val optionElement = if (element.isEmpty) {
      Option.empty
    } else {
      Option(element.head)
    }

    Construction(optionElement, value)
  }

}
