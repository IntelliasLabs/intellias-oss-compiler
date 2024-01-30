package com.intellias.osm.compiler.road.characteristic.area

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.mobility.geo.tools.jts.all._
import com.intellias.mobility.geo.tools.jts.{GeometryBufferThreshold, LineStringUtils}
import com.intellias.osm.compiler.road.FeatureArea
import com.intellias.osm.model.common.FeatureRange
import com.intellias.osm.model.common.wrapper.{LineWrapper, OrderedCoordinateWrapper}
import com.intellias.osm.model.road.{Topology, TopologyNode}
import org.locationtech.jts.geom.{LineString, MultiLineString}

import scala.annotation.tailrec

object RangeBuilder {

  def findRanges(topology: Topology, areas: Seq[FeatureArea]): Seq[FeatureRange] = {
    val line = toLineString(topology.nodes)
    linesToRanges(line, findIntersectedLines(line, areas))
  }

  def findIntersectedLines(line: LineString, areas: Seq[FeatureArea]): Seq[LineString] = {
    val intersectedLines = areas.flatMap { area =>
      val areaPolygon = area.areaGeometry.toJTS
      line.intersection(areaPolygon) match {
        case intersectedLine: LineString if !intersectedLine.isEmpty                => Seq(intersectedLine)
        case intersectedMultiline: MultiLineString if !intersectedMultiline.isEmpty => intersectedMultiline.toLines
        case _                                                                      => Seq.empty
      }
    }

    LineStringUtils.mergeConnectedLines(intersectedLines).toSeq
  }

  @tailrec
  private def linesToRanges(line: LineString, lines: Seq[LineString], acc: Seq[FeatureRange] = Seq.empty): Seq[FeatureRange] = {
    if (lines.isEmpty) acc
    else {
      val intersectedLine = lines.head
      if (intersectedLine.buffer(GeometryBufferThreshold).covers(line)) Seq(FeatureRange.Complete)
      else {
        val start = intersectedLine.getStartPoint.getCoordinate
        val end   = intersectedLine.getEndPoint.getCoordinate
        linesToRanges(line, lines.tail, acc :+ FeatureRange.PositionRange(Wgs84Coordinate(start.x, start.y), Wgs84Coordinate(end.x, end.y)))
      }
    }
  }

  def toLineString(nodes: Seq[TopologyNode]): LineString =
    LineWrapper(nodes.map(n => OrderedCoordinateWrapper(n.nodeIdx, n.latitude, n.longitude))).toJTS
}
