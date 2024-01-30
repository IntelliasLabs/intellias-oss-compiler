package com.intellias.osm.model.poi

//sealed trait PoiStandardCategory extends Serializable

object PoiStandardCategory extends Enumeration {
  val EvChargingStation, FillingStation, General = Value

  private val vals: Map[String, PoiStandardCategory.Value] = Map(
    "POICAT_EV_CHARGING_STATION" -> EvChargingStation,
    "POICAT_FILLING_STATION" -> FillingStation
  )

  def valueOf(str: String): PoiStandardCategory.Value = {
    vals.getOrElse(str, General)
  }
//
//  case object EvChargingStation extends PoiStandardCategory
//  case object FillingStation extends PoiStandardCategory
//  case object General extends PoiStandardCategory
}
