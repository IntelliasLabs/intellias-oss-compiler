package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.schema.model.road.RoadNumLanes
import com.intellias.osm.model.road.Topology
import play.api.libs.json.Writes

import scala.util.Try

object RoadNumLanesExtractor extends RoadCharacteristicsExtractor[RoadNumLanes] with Serializable {
  override val tag = "NDS:NumLanes"
  override implicit def writes: Writes[Seq[RoadNumLanes]] = Writes.seq

  override def decodeFromOsm(topology: Topology): Seq[RoadNumLanes] = {
    topology.tags.get("lanes")
      .flatMap(str => Try(str.toInt).toOption)
      .map(laneNum => Seq(RoadNumLanes(laneNum)))
      .getOrElse(Seq.empty)
  }
}
