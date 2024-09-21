package com.intellias.osm.ndslive

import com.intellias.mobility.geo.tools.nds.NdsTools.coordRoadShiftByte
import com.intellias.osm.model.road.TopologyNode
import org.locationtech.jts.geom.Coordinate

package object road {
  type NdsRoadId = Int
  implicit val coordinateShift: Byte = coordRoadShiftByte
  implicit def nodeToCoordinate(node: TopologyNode): Coordinate = new Coordinate(node.longitude, node.latitude)
}
