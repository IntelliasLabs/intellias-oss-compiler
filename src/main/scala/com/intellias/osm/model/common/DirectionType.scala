package com.intellias.osm.model.common

import com.intellias.osm.model.JsonFormatter

trait DirectionType

object DirectionType extends JsonFormatter[DirectionType] {
  val values: Seq[DirectionType] = Seq(
    Forward, Backward, Both
  )

  case object Forward extends DirectionType
  case object Backward extends DirectionType
  case object Both extends DirectionType

}
