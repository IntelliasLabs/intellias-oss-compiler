package com.intellias.osm.model.road

import com.intellias.osm.model.JsonFormatter

sealed trait CarpoolType

object CarpoolType extends JsonFormatter[CarpoolType] {
  override val values: Seq[CarpoolType] = Seq(Complete, Partial)

  case object Complete extends CarpoolType
  case object Partial extends CarpoolType
}
