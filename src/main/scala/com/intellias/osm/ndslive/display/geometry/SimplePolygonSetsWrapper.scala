package com.intellias.osm.ndslive.display.geometry
import com.intellias.osm.model.common.wrapper.MultiPolygonWrapper
import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.intellias.osm.ndslive.tools.NdsLiveTools._
import nds.core.geometry.{Buffers, GeometryLayerType, SimplePolygonSet2D}
import nds.display.layer.Display2DGeometryLayerList
import nds.display.metadata.Display2DContent

trait SimplePolygonSetsWrapper {
  this: DisplayAreaGeometriesCollector =>

  override val layerType: GeometryLayerType = GeometryLayerType.SIMPLE_POLYGON_SET_2D
  override val displayContentType: Display2DContent = Display2DContent.Values.SIMPLE_AREA_SETS

  override def enrichWithGeometryLayer(geometryList: Display2DGeometryLayerList): Unit = {
    geometryLayer.foreach(geometryList.setSimpleAreaSetDisplayGeometryLayer)
  }

  override protected def wrapGeometries: Buffers = {
    val geometryBuffers = new Buffers(layerType, displayCoordinateShift, displayCoordinateShift, areas.size)
    geometryBuffers.setSimplePolygonSets2D(
      areas.map(area => toPolygonSet(area.geometry)).toArray
    )
    geometryBuffers
  }

  private def toPolygonSet(multiPolygon: MultiPolygonWrapper): SimplePolygonSet2D = {
    val (outlines, holes) = multiPolygon.polygons
      .map(_.toJTS)
      .map(polygon =>
        (polygon.getExteriorRing, (0 until polygon.getNumInteriorRing).map(ringNum => polygon.getInteriorRingN(ringNum)))
      ).unzip
    val polygonSet = new SimplePolygonSet2D(displayCoordinateShift)
    polygonSet.setOutlines(outlines.map(_.toNds).toArray)
    val holesFlattened = holes.flatten
    if (holesFlattened.nonEmpty) {
      polygonSet.setHasHoles(true)
      polygonSet.setHoles(holesFlattened.map(_.toNds).toArray)
    } else {
      polygonSet.setHasHoles(false)
    }
    polygonSet
  }
}
