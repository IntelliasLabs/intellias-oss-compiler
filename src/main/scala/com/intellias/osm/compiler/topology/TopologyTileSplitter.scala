package com.intellias.osm.compiler.topology

import com.intellias.mobility.geo.tools.jts.LineSplitter
import com.intellias.mobility.geo.tools.nds.NdsTileTools
import com.intellias.osm.compiler.geometry.all._
import com.intellias.osm.compiler.topology.TopologyProcessor.getZLevel
import com.intellias.osm.model.road.{Topology, TopologyNode}
import org.locationtech.jts.geom.{Coordinate, LineString}

case class TopologyTileSplitter(tileLevel: Int) extends TopologySplitter {
  def splitByTiles(topology: Topology): List[Topology] = {
    val lineString = topology.toLineString
    val tileIdToBbox = NdsTileTools.collectCoveredTiles(lineString, tileLevel).map(_.toInt)
      .map(tileId => (tileId, NdsTileTools.getTileBoundingBox(tileLevel, tileId)))
      .toMap

    val originalNodes = topology.nodes.map(node => node.toCoordinate -> node).toMap

    val splitedLines = LineSplitter.splitLineOnAreasBorder(tileIdToBbox.values.toList, lineString)

    val newTopologies = tileIdToBbox.flatMap { case (tileId, bbox) =>
      splitedLines
        .filter(line => NdsTileTools.doesTileOwnLineString(bbox, line))
        .zipWithIndex.map{ case (line, idx) =>
          createNewTopology(idx, tileId, topology, line, originalNodes)
        }
    }

    newTopologies.toList
  }

  def createNewTopology(idx: Int, tileId: Int, topology: Topology, line: LineString, originalNodes: Map[Coordinate, TopologyNode]): Topology = {
    topology.copy(
      topologyId = s"${topology.topologyId}-$tileId-$idx",
      tileIds = Array(tileId),
      nodes = rebuildTopoNodes(line, originalNodes, getZLevel(topology.tags))
    )
  }


}
