package com.intellias.osm.ndslive.display.geometry

import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.typesafe.scalalogging.StrictLogging
import nds.core.geometry.{Buffers, GeometryLayerType, IndexBuffer, IndexBufferEntry, Polygon2DBuffers}
import nds.display.layer.Display2DGeometryLayerList
import nds.display.metadata.Display2DContent

trait TriangulatedPolygonsWrapper extends StrictLogging {
  this: DisplayAreaGeometriesCollector =>

  override protected val layerType: GeometryLayerType = GeometryLayerType.POLYGON_2D
  override protected val displayContentType: Display2DContent = Display2DContent.Values.AREAS

  override def enrichWithGeometryLayer(geometryList: Display2DGeometryLayerList): Unit = {
    geometryLayer.foreach(geometryList.setAreaDisplayGeometryLayer)
  }

  override protected def wrapGeometries: Buffers = {
    val geometryBuffers = new Buffers(layerType, displayCoordinateShift, displayCoordinateShift, areas.size)
    val numElements = areas.size // TODO: figure out
    val polygonBuffers = new Polygon2DBuffers(displayCoordinateShift, numElements)
    polygonBuffers.setNumPositions(0) // TODO: ????
    polygonBuffers.setPositions(Array.empty) // TODO: ????
    polygonBuffers.setIndexBuffer(
      new IndexBuffer(0, 0, Array.empty[IndexBufferEntry]) // TODO: ???
    )
    polygonBuffers.setPolygons(Array.empty)
    geometryBuffers.setPolygons2D(polygonBuffers)
    geometryBuffers
  }
}
