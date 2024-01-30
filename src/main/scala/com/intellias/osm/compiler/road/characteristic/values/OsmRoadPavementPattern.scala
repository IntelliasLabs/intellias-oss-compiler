package com.intellias.osm.compiler.road.characteristic.values

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey
import com.intellias.osm.model.road.PavementType

sealed abstract class OsmRoadPavementPattern(val values: Set[String], val pavementType: PavementType) {
  override def toString: String = values.mkString("|")
}

case object OsmRoadPavementPattern extends OsmCompositeKey[OsmRoadPavementPattern] {
  override val values: Seq[OsmRoadPavementPattern] = Seq(
    Asphalt,
    Sandy,
    Gravel,
    Cobblestone,
    Concrete,
    Paved,
    Other,
    Unpaved
  )

  override def groupName: String = "roadClass"

  override def default: OsmRoadPavementPattern = Unknown

  override def apply(value: String): OsmRoadPavementPattern =
    values.find(_.values(value)).getOrElse(Unknown)

  final case object Asphalt extends OsmRoadPavementPattern(Set("asphalt"), PavementType.Asphalt)
  final case object Sandy   extends OsmRoadPavementPattern(Set("sand"), PavementType.Sandy)
  final case object Gravel
      extends OsmRoadPavementPattern(Set("gravel", "fine_gravel", "compacted", "pebblestone", "chipseal"), PavementType.Gravel)
  final case object Cobblestone
      extends OsmRoadPavementPattern(
        Set(
          "cobblestone",
          "sett",
          "paving_stones",
          "paving_stones:lanes",
          "grass_paver",
          "unhewn_cobblestone",
          "paving_stones:30",
          "bricks",
          "brick",
          "cobblestone:flattened"
        ),
        PavementType.Cobblestone
      )
  final case object Concrete extends OsmRoadPavementPattern(Set("concrete", "cement"), PavementType.Concrete)
  final case object Paved    extends OsmRoadPavementPattern(Set("paved", "concrete:plates"), PavementType.Paved)
  final case object Other
      extends OsmRoadPavementPattern(
        Set(
          "wood",
          "metal",
          "tartan",
          "artificial_turf",
          "concrete:lanes",
          "rock",
          "stone",
          "woodchips",
          "acrylic",
          "hard",
          "plastic",
          "metal_grid",
          "rubber",
          "rocks",
          "decoturf"
        ),
        PavementType.Other
      )
  final case object Unpaved
      extends OsmRoadPavementPattern(
        Set("unpaved",
            "ground",
            "dirt",
            "grass",
            "earth",
            "clay",
            "mud",
            "dirt/sand",
            "soil",
            "trail",
            "ground;grass",
            "grass;ground",
            "gravel;ground",
            "boardwalk"),
        PavementType.Unpaved
      )
  final case object Unknown extends OsmRoadPavementPattern(Set(), PavementType.Unknown)

  override val extraValuesForReaders: Seq[OsmRoadPavementPattern] = Seq(Unknown)
}
