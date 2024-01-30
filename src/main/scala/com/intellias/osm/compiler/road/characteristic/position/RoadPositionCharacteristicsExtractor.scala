package com.intellias.osm.compiler.road.characteristic.position

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.road.rules.values.OsmDirection
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{RoadPosition, Topology, TopologyNode}
import play.api.libs.json.Writes

trait RoadPositionCharacteristicsExtractor extends RoadCharacteristicsExtractor[RoadPosition] {
  override implicit def writes: Writes[Seq[RoadPosition]] = Writes.seq
  protected def isDesiredNode(node: TopologyNode): Boolean

  override def decodeFromOsm(feature: Topology): Seq[RoadPosition] =
    feature.nodes.collect {
      case node if isDesiredNode(node) => RoadPosition(Wgs84Coordinate(node.longitude, node.latitude), toDirection(node))
    }

  protected def toDirection(node: TopologyNode): DirectionType = OsmDirection(node.tags.getOrElse("direction", "")).direction
}

