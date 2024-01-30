package com.intellias.osm.compiler

import com.intellias.osm.common.SourceType
import com.intellias.osm.model.display.{DisplayArea, DisplayLine, DisplayPoint, DisplayTile}

package object display {
  case object BuildingFootprintTable extends SourceType[DisplayArea]
  case object DisplayAreaTable extends SourceType[DisplayArea]
  case object DisplayLineTable extends SourceType[DisplayLine]
  case object DisplayPointTable extends SourceType[DisplayPoint]

  case object DisplayTileTable extends SourceType[DisplayTile]
}
