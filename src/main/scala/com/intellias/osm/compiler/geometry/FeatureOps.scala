package com.intellias.osm.compiler.geometry

import com.intellias.mobility.geo.tools.jts.GeoFactory
import com.intellias.osm.model.poi.POI
import com.intellias.osm.model.road.{NdsRoad, Topology, TopologyNode}
import org.locationtech.jts.geom._

trait FeatureOps {

  implicit class TopologyNodeImplicits(topoNode: TopologyNode) {
    def toCoordinate: Coordinate = new Coordinate(topoNode.longitude, topoNode.latitude)
  }

  implicit class TopologyJtsImplicits(topology: Topology) {
    def toLineString: LineString =
      GeoFactory.createLineString(topology.nodes.sortBy(_.nodeIdx).map(_.toCoordinate))
  }

  implicit class NdsRoadImplicits(ndsRoad: NdsRoad) {
    def toLineString: LineString =
      GeoFactory.createLineString(ndsRoad.nodes.sortBy(_.nodeIdx).map(_.toCoordinate))
  }

  implicit class PoiImplicits(poi: POI) {
    def toPoint: Point = GeoFactory.createPoint(new Coordinate(poi.longitude, poi.latitude))
  }

}
