package com.intellias.osm.compiler.schema.model.common

import com.intellias.osm.model.JsonFormatter


sealed trait ReferenceType

object ReferenceType extends JsonFormatter[ReferenceType] {
  override val values: Seq[ReferenceType] = Seq(
    Reference,
    International,
    National,
    Regional,
    Local,
    UnSigned,
    Authority,
    Prow,
    Alternate
  )

  case object Reference extends ReferenceType
  case object International extends ReferenceType
  case object National extends ReferenceType
  case object Regional extends ReferenceType
  case object Local extends ReferenceType
  case object UnSigned extends ReferenceType
  case object Authority extends ReferenceType
  case object Prow extends ReferenceType
  case object Alternate extends ReferenceType

}
