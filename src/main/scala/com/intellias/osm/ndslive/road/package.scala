package com.intellias.osm.ndslive

import com.intellias.mobility.geo.tools.nds.NdsTools.coordRoadShiftByte

package object road {
  type NdsRoadId = Int
  implicit val coordinateShift: Byte = coordRoadShiftByte
}
