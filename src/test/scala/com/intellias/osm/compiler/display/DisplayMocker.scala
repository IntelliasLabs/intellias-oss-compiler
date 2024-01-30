package com.intellias.osm.compiler.display

import com.intellias.osm.model.common.wrapper.{MultiPolygonWrapper, PolygonWrapper}
import com.intellias.osm.model.display.DisplayArea
import com.intellias.osm.model.display.DisplayFeatureType.BuildingFootprint

trait DisplayMocker {
  val mockTileId = 123
  val mockAreaLocalId = 1
  val mockAreaOriginId = 12345678
  def mockDisplayArea(tags: Map[String, String]): DisplayArea = {
    DisplayArea(
      mockTileId,
      mockAreaLocalId,
      s"osm-relation-$mockAreaOriginId",
      MultiPolygonWrapper(Seq.empty[PolygonWrapper]),
      BuildingFootprint.name,
      tags
    )
  }
}
