package com.intellias.osm.ndslive.display.geometry
import com.intellias.osm.model.common.wrapper.MultiPolygonWrapper
import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.intellias.osm.ndslive.tools.NdsLiveTools._
import com.typesafe.scalalogging.StrictLogging
import nds.core.geometry.{Buffers, GeometryLayerType}
import nds.display.layer.Display2DGeometryLayerList
import nds.display.metadata.Display2DContent

trait SimplePolygonWrapper extends StrictLogging {
  this: DisplayAreaGeometriesCollector =>

  override val layerType: GeometryLayerType = GeometryLayerType.SIMPLE_POLYGON_2D
  override val displayContentType: Display2DContent = Display2DContent.Values.SIMPLE_AREAS

  override def enrichWithGeometryLayer(geometryList: Display2DGeometryLayerList): Unit = {
    geometryLayer.foreach(geometryList.setSimpleAreaDisplayGeometryLayer)
  }

  override protected def wrapGeometries: Buffers = {
    val geometryBuffers = new Buffers(layerType, displayCoordinateShift, displayCoordinateShift, areas.size)
    val areasWithComplexGeometry = areas.filter(area => isComplexGeometry(area.geometry))

    if (areasWithComplexGeometry.nonEmpty) {
      logger.warn(
        s"""There are ${areasWithComplexGeometry.length} areas with types ${areasWithComplexGeometry.map(_.featureType).distinct.mkString(", ")}
           |that have complex geometry, i.e. comprising multiple polygons or having holes. Trying to represent them
           |with simple polygons will cause some data loss. Consider using Polygon Sets instead.\nExamples:
           |${areasWithComplexGeometry.take(5).map(_.originalId).mkString(", ")}...""".stripMargin)
    }

    geometryBuffers.setSimplePolygons2D(
      areas
        // to ensure that the enclosed linear ring is taken; holes are ignored - this is what the above warning is about
        .map(area => area.geometry.polygons.head.toJTS.getExteriorRing.toNds)
        .toArray
    )
    geometryBuffers
  }

  private def isComplexGeometry(multiPolygon: MultiPolygonWrapper): Boolean = {
    val polygons = multiPolygon.polygons
    polygons.size > 1 || polygons.exists(_.outer.length > 1) || polygons.exists(_.holes.nonEmpty)
  }
}
