package com.intellias.osm.ndslive.display.attributes.area.building

import com.intellias.osm.compiler.display.attribute.SrcDisplayAreaAttribute
import com.intellias.osm.model.display.DisplayArea
import com.intellias.osm.model.display.DisplayFeatureType.BuildingFootprint
import com.intellias.osm.ndslive.display.attributes.area.NdsDisplayAreaAttributeBuilder

trait BuildingFootprintFilter {
  this: NdsDisplayAreaAttributeBuilder[? <: SrcDisplayAreaAttribute] =>

  override protected def filter(displayArea: DisplayArea): Boolean = {
    displayArea.featureType == BuildingFootprint.name
  }
}
