package com.intellias.osm.ndslive.display.geometry

import com.intellias.osm.model.display.{DisplayFeatureType, DisplayPoint}
import com.intellias.osm.ndslive.display.NdsDisplayTypeMap.PointTypeMap
import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.intellias.osm.ndslive.tools.NdsLiveTools._
import nds.core.geometry.{Buffers, GeometryLayerType}
import nds.core.types.Var4ByteId
import nds.display.instantiations.PointDisplayGeometryLayer
import nds.display.layer.Display2DGeometryLayerList
import nds.display.metadata.Display2DContent
import nds.display.types.DisplayPointType

class DisplayPointGeometriesCollector(points: Seq[DisplayPoint]) extends DisplayGeometriesCollector {
  private lazy val geometryLayer: Option[PointDisplayGeometryLayer] = collect
  private val layerType = GeometryLayerType.POSITION_2D

  private def collect: Option[PointDisplayGeometryLayer] = {
    if (points.nonEmpty) {
      val lineGeometries = new PointDisplayGeometryLayer(layerType, true, true)
      lineGeometries.setCoordShiftXY(displayCoordinateShift)
      lineGeometries.setCoordShiftZ(displayCoordinateShift)
      lineGeometries.setNumElements(points.size)
      lineGeometries.setTypes(extractTypes)
      lineGeometries.setIdentifier(extractLocalIds)
      lineGeometries.setBuffers(wrapGeometries)
      Some(lineGeometries)
    } else {
      None
    }
  }

  override protected def extractLocalIds: Array[Var4ByteId] = {
    points.map(_.localId).map(new Var4ByteId(_)).toArray
  }

  override protected def wrapGeometries: Buffers = {
    val geometryBuffers = new Buffers(layerType, displayCoordinateShift, displayCoordinateShift, points.size)
    geometryBuffers.setPositions2D(
      points.map(line => line.geometry.toJTS.toNds).toArray
    )
    geometryBuffers
  }

  private def extractTypes: Array[DisplayPointType] = {
    points
      .map(line => DisplayFeatureType.lookup(line.featureType))
      .map(lineType => PointTypeMap.getOrElse(lineType, DisplayPointType.DISPLAY_POINT))
      .toArray
  }

  override def getContentType: Option[Display2DContent] = geometryLayer.map(_ => Display2DContent.Values.POINTS)

  override def enrichWithGeometryLayer(geometryList: Display2DGeometryLayerList): Unit = {
    geometryLayer.foreach(geometryList.setPointDisplayGeometryLayer)
  }
}
