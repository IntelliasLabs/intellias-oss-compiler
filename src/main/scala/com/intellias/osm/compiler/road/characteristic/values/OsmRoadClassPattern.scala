package com.intellias.osm.compiler.road.characteristic.values

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey

sealed abstract class OsmRoadClassPattern(val values: Set[String], val classNum: Short) {
  override def toString: String = values.mkString("|")
}

case object OsmRoadClassPattern extends OsmCompositeKey[OsmRoadClassPattern] {
  override val values: Seq[OsmRoadClassPattern] = Seq(
    Class1,
    Class2,
    Class3,
    Class4,
    Class5,
    Class6
  )

  override def groupName: String = "roadClass"

  override def default: OsmRoadClassPattern = Class7

  override def apply(value: String): OsmRoadClassPattern =
    values.find(_.values(value)).getOrElse(Class7)

  final case object Class1 extends OsmRoadClassPattern(Set("motorway", "motorway_link"), 1)
  final case object Class2 extends OsmRoadClassPattern(Set("trunk", "trunk_link"), 2)
  final case object Class3 extends OsmRoadClassPattern(Set("primary", "primary_link"), 3)
  final case object Class4 extends OsmRoadClassPattern(Set("secondary", "secondary_link", "tertiary", "tertiary_link", "mini_roundabout", "junction"), 4)
  final case object Class5
      extends OsmRoadClassPattern(Set("residential",
                                   "unclassified",
                                   "service",
                                   "living_street",
                                   "steps",
                                   "escape",
                                   "raceway",
                                   "bus_stop",
                                   "bridleway",
                                   "cycleway",
                                   "footway",
                                   "pedestrian"),
                               5)
  final case object Class6 extends OsmRoadClassPattern(Set("road", "track", "path"), 6)
  final case object Class7 extends OsmRoadClassPattern(Set(), 7)

  override val extraValuesForReaders: Seq[OsmRoadClassPattern] = Seq(Class7)
}
