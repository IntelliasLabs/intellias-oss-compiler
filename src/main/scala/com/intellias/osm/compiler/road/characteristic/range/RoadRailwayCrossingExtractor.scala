package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.model.common.FeatureRange
import com.intellias.osm.model.road.{RailwayCrossingGate, RailwayCrossingGateType, Topology, TopologyNode}
import play.api.libs.json.Writes

object RoadRailwayCrossingExtractor extends RoadCharacteristicsExtractor[RailwayCrossingGate] {
  override val tag: String                             = "NDS:RailwayCrossing"
  override implicit def writes: Writes[Seq[RailwayCrossingGate]] = Writes.seq

  override def decodeFromOsm(feature: Topology): Seq[RailwayCrossingGate] = {
    feature.nodes
      .filter(node => node.tags.get("railway").contains("level_crossing"))
      .map { node =>
        node.tags.get("crossing:barrier") match {
          case Some("double_half") => RailwayCrossingGateType.DoubleHalf -> toRange(node)
          case Some("full") => RailwayCrossingGateType.Full -> toRange(node)
          case Some("half") => RailwayCrossingGateType.Half -> toRange(node)
          case Some("yes") => RailwayCrossingGateType.Other -> toRange(node)
          case Some("no") => RailwayCrossingGateType.NoGates -> toRange(node)
          case _ => RailwayCrossingGateType.Unknown -> toRange(node)
        }
      }.groupMap{case (gateType, _) => gateType}{case (_, range) => range}
      .map { case (gateType, ranges) =>
        RailwayCrossingGate(gateType, ranges)
      }.toSeq
  }

  private def toRange(node: TopologyNode):FeatureRange =
    FeatureRange.PositionRange(Wgs84Coordinate(node.longitude, node.latitude), Wgs84Coordinate(node.longitude, node.latitude))
}
