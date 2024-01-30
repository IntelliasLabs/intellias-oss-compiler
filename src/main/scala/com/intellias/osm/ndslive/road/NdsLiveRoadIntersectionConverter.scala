package com.intellias.osm.ndslive.road

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.mobility.geo.tools.geotools.GeoTools._
import com.intellias.mobility.geo.tools.nds.NdsTools.{coordNoShiftByte, coordRoadShiftByte}
import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.model.road.{NdsRoad, RoadTile}
import com.intellias.osm.ndslive.tools.NdsLiveTools
import com.intellias.osm.ndslive.tools.NdsLiveTools.calculateLengthCm
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import nds.core.geometry.{Buffers, GeometryLayerType, Line2D, Position2D}
import nds.core.types.{Var4ByteDirectedReference, Var4ByteId}
import nds.road.instantiations.RoadShapesLayer
import nds.road.layer.{RoadGeometryLayer, RoadLayer}
import nds.road.reference.types.IntersectionRoadReference
import nds.road.road.{IntersectionList, Road, RoadList, Intersection => NdsIntersection}
import org.locationtech.jts.geom.Coordinate
import zserio.runtime.io.SerializeUtil

import scala.util.{Failure, Success, Try}

case class NdsLiveRoadIntersectionConverter(conf: NdsLiveConfig) extends NdsConverter[RoadTile] with StrictLogging {
  val roadLayerFileName: Int => String         = tileId => s"${conf.roadLayerFilePrefix}$tileId"
  val roadLayerGeometryFileName: Int => String = tileId => s"${conf.roadGeometryLayerPrefix}$tileId"

  def convert(data: RoadTile): Either[FailedResult[RoadTile], SuccessResult] = {
    Try {
      val topologies                = data.topologies.sortBy(_.ndsRoadId)
      val topoMap                   = topologies.map(t => t.topologyId -> t).toMap
      val intersectionsIn           = data.intersections.sortBy(_.node.nodeId)
      val topoIds: Map[String, Int] = topologies.map(topo => topo.topologyId -> topo.ndsRoadId).toMap

      val roads = topologies.map(t => toRoad(t, topoIds))
      // during the compilation intersections are already grouped by their coordinates but during the output conversion
      // (NDS.Live in this case) due to coordinates shifting and rounding this uniqueness may (and will) be violated
      // therefore here intersections grouping by their coordinates already converted to NDS.Live format is done once again
      val intersections = intersectionsIn
        .map(in => IntersectionInterim(
          NdsLiveTools.position2D(in.node.longitude, in.node.latitude),
          new Coordinate(in.node.longitude, in.node.latitude),
          math.max(in.node.zLevel, 0).toByte,
          in.node.isVirtual,
          in.topologyIds
        ))
        .groupBy(in => (in.position, in.zLevel))
        .map {
          case (_, intersections) => merge(intersections)
        }
        .zipWithIndex
        .map {
          case (interim, idx) => toIntersection(interim, idx + 1, topoIds, topoMap)
        }.toArray

      val roadLayer = new RoadLayer(
        coordRoadShiftByte,
        new RoadList(roads),
        new IntersectionList(coordRoadShiftByte, intersections)
      )

      val raodGeometries: Array[Line2D] = topologies.map(t => NdsLiveTools.line2D(t.nodes.map(n => Wgs84Coordinate(n.longitude, n.latitude))))

      val buffers: Buffers = new Buffers(GeometryLayerType.LINE_2D, coordRoadShiftByte, coordNoShiftByte, raodGeometries.length)
      buffers.setLines2D(raodGeometries)

      val roadShapesLayer = new RoadShapesLayer(GeometryLayerType.LINE_2D, true, false)
      roadShapesLayer.setCoordShiftXY(coordRoadShiftByte)
      roadShapesLayer.setCoordShiftZ(coordNoShiftByte)
      roadShapesLayer.setNumElements(roads.length)
      roadShapesLayer.setIdentifier(topologies.map(t => topoIds(t.topologyId)).map(new Var4ByteId(_)))
      roadShapesLayer.setBuffers(buffers)

      val roadGeometryLayer = new RoadGeometryLayer(roadShapesLayer)

      SuccessResult(
        Array(
          (roadLayerFileName(data.tileId), SerializeUtil.serializeToBytes(roadLayer)),
          (roadLayerGeometryFileName(data.tileId), SerializeUtil.serializeToBytes(roadGeometryLayer))
        ))

    } match {
      case Success(r) => Right(r)
      case Failure(exception) =>
        logger.error(s"Can't build roads for tileId: ${data.tileId}", exception)
        Left(FailedResult(exception.getMessage, data))
    }
  }

  private def toRoad(topo: NdsRoad, topoIds: Map[String, Int]): Road = {
    new Road(
      new Var4ByteId(topoIds(topo.topologyId)),
      calculateLengthCm(topo.nodes.map(n => Wgs84Coordinate(n.longitude, n.latitude)))
    )
  }
  private def toIntersection(inter: IntersectionInterim,
                             intersectionId: Int,
                             topoIds: Map[String, Int],
                             topologies: Map[String, NdsRoad]): NdsIntersection = {
    new NdsIntersection(
      coordRoadShiftByte,
      intersectionId,
      inter.isVirtual,
      inter.zLevel,
      inter.topologies.length.toShort,
      inter.position,
      inter.topologies.map { id =>
        new IntersectionRoadReference(
          inter.isVirtual,
          new Var4ByteDirectedReference(topoIds(id)),
          calculateAngle(inter, topologies(id))
        )
      }
    )
  }

  private def merge(intersections: Array[IntersectionInterim]): IntersectionInterim = {
    // position and z-level will be the same across all the elements as they were considered in grouping statement
    intersections.reduceLeft((merged, other) => merged.copy(
      isVirtual = merged.isVirtual || other.isVirtual,
      topologies = merged.topologies ++ other.topologies
    ))
  }

  private def calculateAngle(inter: IntersectionInterim, topology: NdsRoad): Short = {
    val refNode = topology.nodes.head
    val nonRefNode = topology.nodes.last
    val refCoord = new Coordinate(refNode.longitude, refNode.latitude)
    val nonRefCoord = new Coordinate(nonRefNode.longitude, nonRefNode.latitude)
    // to find at which end of the topology segment the intersection is we cannot rely on node IDs since some of the nodes
    // may be created as a result of nodes merge and information about some node IDs may be lost
    val azimuthDegrees = if (calculateDistance(inter.positionWgs, refCoord) < calculateDistance(inter.positionWgs, nonRefCoord)) {
      val vectorEnd = topology.nodes(1)
      azimuth(refCoord, new Coordinate(vectorEnd.longitude, vectorEnd.latitude))
    } else {
      val vectorEnd = topology.nodes(topology.nodes.length - 2)
      azimuth(nonRefCoord, new Coordinate(vectorEnd.longitude, vectorEnd.latitude))
    }
    // convert to NDS.Live compatible format: ...sectors of ~1.4 degrees. Value 0 indicates north; values increase clockwise.
    (azimuthDegrees / 1.41).toShort
  }

  private case class IntersectionInterim(position: Position2D,
                                         positionWgs: Coordinate,
                                         zLevel: Byte,
                                         isVirtual: Boolean,
                                         topologies: Array[String])
}
