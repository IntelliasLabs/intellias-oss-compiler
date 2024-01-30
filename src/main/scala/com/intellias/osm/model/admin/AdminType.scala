package com.intellias.osm.model.admin

import com.intellias.osm.model.JsonFormatter

sealed trait AdminType
object AdminType extends JsonFormatter[AdminType] {
  val values: Seq[AdminType] = Seq(
    Country,
    SubCountrySet,
    SubCountry,
    County,
    Municipality,
    MunicipalitySubdivision,
    Hamlet,
    Neighborhood,
    CityBlock
  )

  case object Country                 extends AdminType
  case object SubCountry              extends AdminType
  case object SubCountrySet           extends AdminType
  case object County                  extends AdminType
  case object Municipality            extends AdminType
  case object MunicipalitySubdivision extends AdminType
  case object Hamlet                  extends AdminType
  case object Neighborhood            extends AdminType
  case object CityBlock               extends AdminType
}
