package com.intellias.osm.compiler.topology

import com.intellias.mobility.geo.tools.common.CoordinateConversion
import com.intellias.mobility.geo.tools.nds.NdsTileTools
import com.intellias.osm.model.road.TopologyNode
import org.locationtech.jts.geom.{Coordinate, LineString}

trait TopologySplitter {
  def tileLevel: Int

  def rebuildTopoNodes(line: LineString, coordinateToNode: Map[Coordinate, TopologyNode], zLevel: Int): Array[TopologyNode] = {
    val coordinates = line.getCoordinates
    val rebuiltNodes = coordinates.zipWithIndex.map {
      case (coord, idx) if coordinateToNode.contains(coord) => coordinateToNode(coord).copy(nodeIdx = idx)
      case (coord, idx) => TopologyNode(
        nodeIdx = idx,
        nodeId = CoordinateConversion.toMortonCode(coord.x, coord.y),
        longitude = coord.x,
        latitude = coord.y,
        isIntersectNode = false,
        isFirstOrLastNode = idx == 0 || idx == coordinates.length - 1,
        zLevel = zLevel,
        nodeTileId = NdsTileTools.getPackedTileId(tileLevel, coord.x, coord.y),
        tags = Map.empty,
        isVirtual = true
      )
    }
    patchFirstLastNode(rebuiltNodes.head) +: rebuiltNodes.tail.dropRight(1) :+ patchFirstLastNode(rebuiltNodes.last)
  }

  private def patchFirstLastNode(node: TopologyNode): TopologyNode = {
    if (node.isFirstOrLastNode || node.isIntersectNode) {
      node
    } else {
      node.copy(isFirstOrLastNode = true)
    }
  }
}
