package com.intellias.osm.model.poi

import com.intellias.osm.model.JsonFormatter

sealed trait ElectricCurrentType

object ElectricCurrentType extends JsonFormatter[ElectricCurrentType] {
  override val values: Seq[ElectricCurrentType] = Seq(AC, DC)

  case object AC extends ElectricCurrentType
  case object DC extends ElectricCurrentType
}
