package com.intellias.osm.compiler.road.rules.values

import com.intellias.osm.model.JsonFormatter

trait HazmatType

object HazmatType extends JsonFormatter[HazmatType] {
  val values: Seq[HazmatType] = Seq(
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
  case object No        extends HazmatType
  case object Water     extends HazmatType
  case object Explosive extends HazmatType
  case object A         extends HazmatType
  case object B         extends HazmatType
  case object C         extends HazmatType
  case object D         extends HazmatType
  case object E         extends HazmatType

  case object TypeUSA1   extends HazmatType
  case object TypeUSA1_1 extends HazmatType
  case object TypeUSA1_2 extends HazmatType
  case object TypeUSA1_3 extends HazmatType
  case object TypeUSA2   extends HazmatType
  case object TypeUSA2_1 extends HazmatType
  case object TypeUSA3   extends HazmatType
  case object TypeUSA4   extends HazmatType
  case object TypeUSA5   extends HazmatType
  case object TypeUSA5_1 extends HazmatType
  case object TypeUSA5_2 extends HazmatType
  case object TypeUSA6   extends HazmatType
  case object TypeUSA6_1 extends HazmatType
  case object TypeUSA0   extends HazmatType

  case object TypeChina1 extends HazmatType
  case object TypeChina2 extends HazmatType
  case object TypeChina3 extends HazmatType
  case object TypeChina4 extends HazmatType
  case object TypeChina5 extends HazmatType
}
