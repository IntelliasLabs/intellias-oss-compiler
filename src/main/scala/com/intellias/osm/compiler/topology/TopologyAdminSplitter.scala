package com.intellias.osm.compiler.topology

import com.intellias.mobility.geo.tools.common.Side.{LeftSide, RightSide}
import com.intellias.mobility.geo.tools.jts.all._
import com.intellias.mobility.geo.tools.jts.{GeometryBufferThreshold, LineSplitter, LineToPolygonSideDetector}
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.geometry.all._
import com.intellias.osm.compiler.topology.TopologyAdminSplitter.{LeftAdmin, LineToAdmin, RightAdmin, TopologyAdmins}
import com.intellias.osm.compiler.topology.TopologyProcessor.getZLevel
import com.intellias.osm.model.admin.{AdminPlaceGround, FeatureAdminPlace}
import com.intellias.osm.model.road.{Topology, TopologyNode}
import org.locationtech.jts.geom.{Coordinate, LineString, MultiPolygon}

case class TopologyAdminSplitter(tileLevel: Int, env: AdminPlaceService) extends TopologySplitter {

  def splitByAdmins(topology: Topology, adminPlaces: List[AdminPlaceGround]): List[Topology] = {
    if (adminPlaces.nonEmpty) {
      val admins: List[(AdminPlaceGround, MultiPolygon)] = adminPlaces.map(a => (a, a.area.toJTS))
      val splitedLines                                   = LineSplitter.splitLineOnAreasBorder(admins.map(_._2), topology.toLineString)

      val originalNodes: Map[Coordinate, TopologyNode] = topology.nodes.map { node: TopologyNode =>
        node.toCoordinate -> node
      }.toMap

      val lineToAdmins: List[LineToAdmin] = splitedLines
        .map(line => (line, matchAdmins(line, admins)))
        .map {
          case (line, admins) =>
            LineToAdmin(
              line,
              env.adminPlaceService.resolveFeatureAdminPlace(admins.left),
              env.adminPlaceService.resolveFeatureAdminPlace(admins.right)
            )
        }

      val newTopologies = lineToAdmins.zipWithIndex.map {
        case (lToA, idx) => createNewTopology(idx, topology, lToA, originalNodes)
      }
      newTopologies
    } else {
      List(topology)
    }
  }

  def createNewTopology(idx: Int, topology: Topology, lineToAdmin: LineToAdmin, coordinateToNode: Map[Coordinate, TopologyNode]): Topology = {
    val newNodes = rebuildTopoNodes(lineToAdmin.line, coordinateToNode, getZLevel(topology.tags))

    topology.copy(
      topologyId = s"${topology.topologyId}-$idx",
      leftAdmin = lineToAdmin.left,
      rightAdmin = lineToAdmin.right,
      tileIds = newNodes.map(_.nodeTileId).distinct,
      nodes = newNodes
    )
  }

  def matchAdmins(line: LineString, admins: List[(AdminPlaceGround, MultiPolygon)]): TopologyAdmins = {
    admins
      .filter(admin => admin._2.buffer(GeometryBufferThreshold).covers(line))
      .flatMap { admin =>
        if (!admin._2.isOnBoundary(line)) List(LeftAdmin(admin._1), RightAdmin(admin._1))
        else {
          admin._2
            .findPolygonAdjacentToLine(line)
            .map { polygon =>
              LineToPolygonSideDetector.identifyPolygonSideRelativeToLine(line, polygon)
            }
            .map {
              case LeftSide  => LeftAdmin(admin._1)
              case RightSide => RightAdmin(admin._1)
            }
            .map(List(_))
            .getOrElse(List.empty)
        }
      }
      .foldLeft(TopologyAdmins()) {
        case (acc, LeftAdmin(admin))  => acc.copy(left = acc.left :+ admin)
        case (acc, RightAdmin(admin)) => acc.copy(right = acc.right :+ admin)
      }
  }

}

object TopologyAdminSplitter {
  sealed trait MatchedAdmin                      extends Serializable
  case class LeftAdmin(admin: AdminPlaceGround)  extends MatchedAdmin
  case class RightAdmin(admin: AdminPlaceGround) extends MatchedAdmin
  case class LineToAdmin(line: LineString, left: Option[FeatureAdminPlace], right: Option[FeatureAdminPlace])
  case class TopologyAdmins(left: List[AdminPlaceGround] = List.empty, right: List[AdminPlaceGround] = List.empty)
}
