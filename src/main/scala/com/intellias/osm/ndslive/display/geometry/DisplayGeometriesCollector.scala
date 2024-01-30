package com.intellias.osm.ndslive.display.geometry

import nds.core.geometry.Buffers
import nds.core.types.Var4ByteId
import nds.display.layer.Display2DGeometryLayerList
import nds.display.metadata.Display2DContent

trait DisplayGeometriesCollector {
  def getContentType: Option[Display2DContent]
  def enrichWithGeometryLayer(geometryList: Display2DGeometryLayerList): Unit
  protected def extractLocalIds: Array[Var4ByteId]
  protected def wrapGeometries: Buffers
}
