package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.road.highway
import com.intellias.osm.model.common.FeatureRange
import com.intellias.osm.model.road.{PedestrianCrossing, Topology, TopologyNode}
import play.api.libs.json.Writes

object RoadHasPedestrianCrossingExtractor extends RoadCharacteristicsExtractor[PedestrianCrossing] {
  override val tag              = "NDS:PedestrianCrossing"
  override implicit def writes: Writes[Seq[PedestrianCrossing]] = Writes.seq

  override def decodeFromOsm(topology: Topology): Seq[PedestrianCrossing] = {
    val ranges: Array[FeatureRange.PositionRange] = topology.nodes.collect {
      case node if isPedestrianNode(node) =>
        FeatureRange.PositionRange(Wgs84Coordinate(node.longitude, node.latitude), Wgs84Coordinate(node.longitude, node.latitude))
    }



    if (ranges.nonEmpty && !RoadTypeFormExtractor.isPedestrian(topology)) {
      Seq(PedestrianCrossing(ranges.toSeq))
    } else Seq.empty

  }

  val isPedestrianNode: TopologyNode => Boolean = node => node.tagValue(highway, "crossing")
}
