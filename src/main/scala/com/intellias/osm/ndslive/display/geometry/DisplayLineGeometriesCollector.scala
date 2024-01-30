package com.intellias.osm.ndslive.display.geometry

import com.intellias.osm.model.display.{DisplayFeatureType, DisplayLine}
import com.intellias.osm.ndslive.display.NdsDisplayTypeMap.LineTypeMap
import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.intellias.osm.ndslive.tools.NdsLiveTools._
import nds.core.geometry.{Buffers, GeometryLayerType}
import nds.core.types.Var4ByteId
import nds.display.instantiations.LineDisplayGeometryLayer
import nds.display.layer.Display2DGeometryLayerList
import nds.display.metadata.Display2DContent
import nds.display.types.DisplayLineType

class DisplayLineGeometriesCollector(lines: Seq[DisplayLine]) extends DisplayGeometriesCollector {
  private lazy val geometryLayer: Option[LineDisplayGeometryLayer] = collect
  private val layerType = GeometryLayerType.LINE_2D

  private def collect: Option[LineDisplayGeometryLayer] = {
    if (lines.nonEmpty) {
      val lineGeometries = new LineDisplayGeometryLayer(layerType, true, true)
      lineGeometries.setCoordShiftXY(displayCoordinateShift)
      lineGeometries.setCoordShiftZ(displayCoordinateShift)
      lineGeometries.setNumElements(lines.size)
      lineGeometries.setTypes(extractTypes)
      lineGeometries.setIdentifier(extractLocalIds)
      lineGeometries.setBuffers(wrapGeometries)
      Some(lineGeometries)
    } else {
      None
    }
  }

  override protected def extractLocalIds: Array[Var4ByteId] = {
    lines.map(_.localId).map(new Var4ByteId(_)).toArray
  }

  override protected def wrapGeometries: Buffers = {
    val geometryBuffers = new Buffers(layerType, displayCoordinateShift, displayCoordinateShift, lines.size)
    geometryBuffers.setLines2D(
      lines.map(line => line.geometry.toJTS.toNds).toArray
    )
    geometryBuffers
  }

  private def extractTypes: Array[DisplayLineType] = {
    lines
      .map(line => DisplayFeatureType.lookup(line.featureType))
      .map(lineType => LineTypeMap.getOrElse(lineType, DisplayLineType.DISPLAY_LINE))
      .toArray
  }

  override def getContentType: Option[Display2DContent] = geometryLayer.map(_ => Display2DContent.Values.LINES)

  override def enrichWithGeometryLayer(geometryList: Display2DGeometryLayerList): Unit = {
    geometryLayer.foreach(geometryList.setLineDisplayGeometryLayer)
  }
}
