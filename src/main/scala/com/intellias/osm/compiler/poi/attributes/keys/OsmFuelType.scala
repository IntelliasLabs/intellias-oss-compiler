package com.intellias.osm.compiler.poi.attributes.keys

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey
import com.intellias.osm.model.poi.EnergyType


sealed abstract class OsmFuelType(val values: Set[String], val energyType: EnergyType) {
  override def toString: String = values.mkString("|")
}

case object OsmFuelType extends OsmCompositeKey[OsmFuelType] {
  override val values: Seq[OsmFuelType] = Seq(
    Cng,
    Diesel,
    E85,
    Ethanol,
    Gasoline80,
    Gasoline87,
    Gasoline91,
    Gasoline92,
    Gasoline95,
    Gasoline98,
    Gasoline100,
    Lpg
  )

  override def groupName: String = "energyType"

  override def default: OsmFuelType = Cng

  override def apply(value: String): OsmFuelType =
    values.find (_.values(value)).getOrElse(Electricity)

  final case object Electricity extends OsmFuelType(Set(""), EnergyType.Electricity)
  final case object Cng         extends OsmFuelType(Set("cng"), EnergyType.Cng)
  final case object Diesel      extends OsmFuelType(Set("diesel"), EnergyType.Diesel)
  final case object E85         extends OsmFuelType(Set("e85"), EnergyType.E85)
  final case object Ethanol     extends OsmFuelType(Set("ethanol", "alcohol"), EnergyType.Ethanol)
  final case object Gasoline80  extends OsmFuelType(Set("octane_80"), EnergyType.Gasoline80)
  final case object Gasoline87  extends OsmFuelType(Set("octane_87"), EnergyType.Gasoline87)
  final case object Gasoline91  extends OsmFuelType(Set("octane_91"), EnergyType.Gasoline91)
  final case object Gasoline92  extends OsmFuelType(Set("octane_92"), EnergyType.Gasoline92)
  final case object Gasoline95  extends OsmFuelType(Set("octane_95"), EnergyType.Gasoline95)
  final case object Gasoline98  extends OsmFuelType(Set("octane_98"), EnergyType.Gasoline98)
  final case object Gasoline100 extends OsmFuelType(Set("octane_100"), EnergyType.Gasoline100)
  final case object Lpg         extends OsmFuelType(Set("lpg"), EnergyType.Lpg)



  override val extraValuesForReaders: Seq[OsmFuelType] = Seq(Electricity)
}
