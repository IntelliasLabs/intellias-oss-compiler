package com.intellias.osm.compiler.road.rules.values

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey

sealed abstract class OsmHazmatType(val value: String, val hazmatType: HazmatType) {
  override def toString: String = value
}

object OsmHazmatType extends OsmCompositeKey[OsmHazmatType] {
  override val values: Seq[OsmHazmatType] = Seq(
    No,
    Water,
    Explosive,
    A,
    B,
    C,
    D,
    E,
    TypeUSA1,
    TypeUSA1_1,
    TypeUSA1_2,
    TypeUSA1_3,
    TypeUSA2,
    TypeUSA2_1,
    TypeUSA3,
    TypeUSA4,
    TypeUSA5,
    TypeUSA5_1,
    TypeUSA5_2,
    TypeUSA6,
    TypeUSA6_1,
    TypeUSA0,
    TypeChina1,
    TypeChina2,
    TypeChina3,
    TypeChina4,
    TypeChina5
  )

  override def groupName: String = "hazmatType"

  override def default: OsmHazmatType = No

  final case object No extends OsmHazmatType("", HazmatType.No)

  final case object Water extends OsmHazmatType("water", HazmatType.Water)

  final case object Explosive extends OsmHazmatType("explosive", HazmatType.Explosive)

  final case object A extends OsmHazmatType("A", HazmatType.A)
  final case object B extends OsmHazmatType("B", HazmatType.B)
  final case object C extends OsmHazmatType("C", HazmatType.C)
  final case object D extends OsmHazmatType("D", HazmatType.D)
  final case object E extends OsmHazmatType("E", HazmatType.E)

  final case object TypeUSA1   extends OsmHazmatType("1", HazmatType.TypeUSA1)
  final case object TypeUSA1_1 extends OsmHazmatType("1.1", HazmatType.TypeUSA1_1)
  final case object TypeUSA1_2 extends OsmHazmatType("1.2", HazmatType.TypeUSA1_2)
  final case object TypeUSA1_3 extends OsmHazmatType("1.3", HazmatType.TypeUSA1_3)
  final case object TypeUSA2   extends OsmHazmatType("2", HazmatType.TypeUSA2)
  final case object TypeUSA2_1 extends OsmHazmatType("2.1", HazmatType.TypeUSA2_1)
  final case object TypeUSA3   extends OsmHazmatType("3", HazmatType.TypeUSA3)
  final case object TypeUSA4   extends OsmHazmatType("4", HazmatType.TypeUSA4)
  final case object TypeUSA5   extends OsmHazmatType("5", HazmatType.TypeUSA5)
  final case object TypeUSA5_1 extends OsmHazmatType("5.1", HazmatType.TypeUSA5_1)
  final case object TypeUSA5_2 extends OsmHazmatType("5.2", HazmatType.TypeUSA5_2)
  final case object TypeUSA6   extends OsmHazmatType("6", HazmatType.TypeUSA6)
  final case object TypeUSA6_1 extends OsmHazmatType("6.1", HazmatType.TypeUSA6_1)
  final case object TypeUSA0   extends OsmHazmatType("0", HazmatType.TypeUSA0)

  final case object TypeChina1 extends OsmHazmatType("HK:1", HazmatType.TypeChina1)
  final case object TypeChina2 extends OsmHazmatType("HK:2", HazmatType.TypeChina2)
  final case object TypeChina3 extends OsmHazmatType("HK:3", HazmatType.TypeChina3)
  final case object TypeChina4 extends OsmHazmatType("HK:4", HazmatType.TypeChina4)
  final case object TypeChina5 extends OsmHazmatType("HK:5", HazmatType.TypeChina5)

}
