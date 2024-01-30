package com.intellias.osm.ndslive.display.geometry

import com.intellias.osm.model.display.{DisplayArea, DisplayFeatureType}
import com.intellias.osm.ndslive.display.NdsDisplayTypeMap.AreaTypeMap
import com.intellias.osm.ndslive.display.displayCoordinateShift
import nds.core.geometry.GeometryLayerType
import nds.core.types.Var4ByteId
import nds.display.instantiations.AreaDisplayGeometryLayer
import nds.display.metadata.Display2DContent
import nds.display.types.DisplayAreaType

abstract class DisplayAreaGeometriesCollector(val areas: Seq[DisplayArea]) extends DisplayGeometriesCollector {
  protected lazy val geometryLayer: Option[AreaDisplayGeometryLayer] = collect

  private def collect: Option[AreaDisplayGeometryLayer] = {
    if (areas.isEmpty) {
      None
    } else {
      val areaTypes = areas
        .map(area => DisplayFeatureType.lookup(area.featureType))
        // fall-back to the generic area type if no mapping provided
        .map(featureType => AreaTypeMap.getOrElse(featureType, DisplayAreaType.DISPLAY_AREA))
        .toArray
      val areaGeometries = new AreaDisplayGeometryLayer(layerType, true, true)
      areaGeometries.setCoordShiftXY(displayCoordinateShift)
      areaGeometries.setCoordShiftZ(displayCoordinateShift)
      areaGeometries.setNumElements(areas.size)
      areaGeometries.setTypes(areaTypes)
      areaGeometries.setIdentifier(extractLocalIds)
      areaGeometries.setBuffers(wrapGeometries)
      Some(areaGeometries)
    }
  }

  override def getContentType: Option[Display2DContent] = geometryLayer.map(_ => displayContentType)

  override protected def extractLocalIds: Array[Var4ByteId] = {
    areas.map(_.localId).map(new Var4ByteId(_)).toArray
  }

  protected val layerType: GeometryLayerType
  protected val displayContentType: Display2DContent
}
