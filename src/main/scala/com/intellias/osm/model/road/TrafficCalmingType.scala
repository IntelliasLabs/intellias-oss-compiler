package com.intellias.osm.compiler.schema.model.road

import com.intellias.osm.model.JsonFormatter


trait TrafficCalmingType

object TrafficCalmingType extends JsonFormatter[TrafficCalmingType] {
  override val values: Seq[TrafficCalmingType] = Seq(
    Bump,
    MiniBumps,
    Hump,
    Table,
    Cushion,
    RumbleStrip,
    Dip,
    DoubleDip,
    DynamicBump,
    Chicane,
    Choker,
    Island,
    ChokedIsland,
    PaintedIsland,
    ChokedTable,
    Other
  )

  case object Bump extends TrafficCalmingType
  case object MiniBumps extends TrafficCalmingType
  case object Hump extends TrafficCalmingType
  case object Table extends TrafficCalmingType
  case object Cushion extends TrafficCalmingType
  case object RumbleStrip extends TrafficCalmingType
  case object Dip extends TrafficCalmingType
  case object DoubleDip extends TrafficCalmingType
  case object DynamicBump extends TrafficCalmingType
  case object Chicane extends TrafficCalmingType
  case object Choker extends TrafficCalmingType
  case object Island extends TrafficCalmingType
  case object ChokedIsland extends TrafficCalmingType
  case object PaintedIsland extends TrafficCalmingType
  case object ChokedTable extends TrafficCalmingType
  case object Other extends TrafficCalmingType

}
