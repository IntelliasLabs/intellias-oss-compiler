package com.intellias.osm.compiler.road.characteristic

import com.intellias.osm.model.road.{Topology, TopologyNode}

import scala.language.implicitConversions

object TopologySpec {
  implicit def mapToTopology(tags: Map[String,String]): Topology = mapToTopology(tags2 = tags)

  implicit def mapToTopology(tags2: Map[String,String], nodes: Array[TopologyNode] = Array(createDummyNode())): Topology = Topology(
    topologyId = "1",
    originId = 1,
    nodes = nodes,
    tags = tags2,
    relations = List.empty,
    tileIds = Array.empty
  )

  def createDummyNode(lonLat: (Double, Double) = (1.1, 1.1), tags: Map[String, String] = Map.empty): TopologyNode = TopologyNode(
    nodeIdx = 0,
    nodeId = 111,
    longitude = lonLat._1,
    latitude = lonLat._2,
    isIntersectNode = false,
    isFirstOrLastNode = true,
    zLevel = 0,
    nodeTileId = 545366462,
    tags = tags)
}
