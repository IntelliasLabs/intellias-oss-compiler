package com.intellias.osm.model.road

import com.intellias.osm.model.JsonFormatter

trait PavementType

object PavementType extends JsonFormatter[PavementType] {
  val values: Seq[PavementType] = Seq(
    Asphalt, Sandy, Gravel, Cobblestone, Concrete, Paved, Other, Unpaved, Unknown
  )

  case object Asphalt extends PavementType
  case object Sandy extends PavementType
  case object Gravel extends PavementType
  case object Cobblestone extends PavementType
  case object Concrete extends PavementType
  case object Paved extends PavementType
  case object Other extends PavementType
  case object Unpaved extends PavementType
  case object Unknown extends PavementType
}
