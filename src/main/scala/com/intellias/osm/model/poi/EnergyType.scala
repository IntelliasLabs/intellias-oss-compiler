package com.intellias.osm.model.poi

import com.intellias.osm.model.JsonFormatter


sealed trait EnergyType

case object EnergyType extends JsonFormatter[EnergyType]{
  val values: Seq[EnergyType] = Seq(
    Electricity,
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

  final case object Electricity extends EnergyType
  final case object Cng         extends EnergyType
  final case object Diesel      extends EnergyType
  final case object E85         extends EnergyType
  final case object Ethanol     extends EnergyType
  final case object Gasoline80  extends EnergyType
  final case object Gasoline87  extends EnergyType
  final case object Gasoline91  extends EnergyType
  final case object Gasoline92  extends EnergyType
  final case object Gasoline95  extends EnergyType
  final case object Gasoline98  extends EnergyType
  final case object Gasoline100 extends EnergyType
  final case object Lpg         extends EnergyType
}
