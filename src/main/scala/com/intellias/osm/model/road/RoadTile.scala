package com.intellias.osm.model.road

case class RoadTile(tileId: Int, topologies: Array[NdsRoad], intersections: Array[Intersection])
