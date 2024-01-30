package com.intellias.osm.model.road

import com.intellias.osm.model.JsonFormatter

sealed trait RailwayCrossingGateType

object RailwayCrossingGateType extends JsonFormatter[RailwayCrossingGateType]{
  override val values: Seq[RailwayCrossingGateType] = Seq(
    DoubleHalf,
    Full,
    Half,
    Other,
    NoGates,
    Unknown
  )

  final case object DoubleHalf extends RailwayCrossingGateType
  final case object Full extends RailwayCrossingGateType
  final case object Half extends RailwayCrossingGateType
  final case object Other extends RailwayCrossingGateType
  final case object NoGates extends RailwayCrossingGateType
  final case object Unknown extends RailwayCrossingGateType
}
