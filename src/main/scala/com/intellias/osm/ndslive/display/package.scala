package com.intellias.osm.ndslive

package object display {
  implicit val displayCoordinateShift: Byte = 0.toByte // So far display layer does not support any coordinate shift except 0
}
