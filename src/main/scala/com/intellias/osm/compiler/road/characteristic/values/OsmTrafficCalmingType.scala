package com.intellias.osm.compiler.road.characteristic.values

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey
import com.intellias.osm.compiler.schema.model.road.TrafficCalmingType

/*
 OSM wiki: https://wiki.openstreetmap.org/wiki/Key%3Atraffic_calming
 */
class OsmTrafficCalmingType (val value: String, val calmingType: TrafficCalmingType) {
  override def toString: String = value
}

object OsmTrafficCalmingType extends OsmCompositeKey[OsmTrafficCalmingType] {
  override val values: Seq[OsmTrafficCalmingType] = Seq(
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
    ChokedTable
  )

  override val extraValuesForReaders: Seq[OsmTrafficCalmingType] = Seq(Other)

  override def groupName: String = "calmingType"

  override def default: OsmTrafficCalmingType = OsmTrafficCalmingType.Other

  case object Bump extends OsmTrafficCalmingType("bump",  TrafficCalmingType.Bump)

  case object MiniBumps extends OsmTrafficCalmingType("mini_bumps",  TrafficCalmingType.MiniBumps)

  case object Hump extends OsmTrafficCalmingType("hump",  TrafficCalmingType.Hump)

  case object Table extends OsmTrafficCalmingType("table",  TrafficCalmingType.Table)

  case object Cushion extends OsmTrafficCalmingType("cushion",  TrafficCalmingType.Cushion)

  case object RumbleStrip extends OsmTrafficCalmingType("rumble_strip",  TrafficCalmingType.RumbleStrip)

  case object Dip extends OsmTrafficCalmingType("dip",  TrafficCalmingType.Dip)

  case object DoubleDip extends OsmTrafficCalmingType("double_dip",  TrafficCalmingType.DoubleDip)

  case object DynamicBump extends OsmTrafficCalmingType("dynamic_bump",  TrafficCalmingType.DynamicBump)

  case object Chicane extends OsmTrafficCalmingType("chicane",  TrafficCalmingType.Chicane)

  case object Choker extends OsmTrafficCalmingType("choker",  TrafficCalmingType.Choker)

  case object Island extends OsmTrafficCalmingType("island",  TrafficCalmingType.Island)

  case object ChokedIsland extends OsmTrafficCalmingType("choked_island",  TrafficCalmingType.ChokedIsland)

  case object PaintedIsland extends OsmTrafficCalmingType("painted_island",  TrafficCalmingType.PaintedIsland)

  case object ChokedTable extends OsmTrafficCalmingType("choked_table",  TrafficCalmingType.ChokedTable)

  case object Other extends OsmTrafficCalmingType("",  TrafficCalmingType.Other)

}
