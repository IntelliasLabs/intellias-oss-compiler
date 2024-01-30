package com.intellias.osm.ndslive.display.attributes.area.building

import com.intellias.osm.compiler.display.attribute.value.area.Color
import com.intellias.osm.compiler.display.attribute.value.area.Color._
import nds.core.color.ColorRgba

trait ColorConverter {
  def toNdsColor(color: Color): Option[ColorRgba] = {
    color match {
      // last parameter is for opacity
      case White => Some(new ColorRgba(255, 255, 255, 1))
      case Grey => Some(new ColorRgba(128, 128, 128, 1))
      case Brown => Some(new ColorRgba(165, 42, 42, 1))
      case Red => Some(new ColorRgba(255, 0, 0, 1))
      case Yellow => Some(new ColorRgba(255, 255, 0, 1))
      case Beige => Some(new ColorRgba(245, 245, 220, 1))
      case Black => Some(new ColorRgba(0, 0, 0, 1))
      case Green => Some(new ColorRgba(0, 128, 0, 1))
      case Orange => Some(new ColorRgba(255, 165, 0, 1))
      case _ => None
    }
  }
}
