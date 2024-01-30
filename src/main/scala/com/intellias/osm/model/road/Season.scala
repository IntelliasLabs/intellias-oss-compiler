package com.intellias.osm.model.road

import com.intellias.osm.model.JsonFormatter

trait Season {}

object Season extends JsonFormatter[Season] {

  val values: Seq[Season] = Seq(
    Winter,
    Spring,
    Summer,
    Autumn,
    DrySeason,
    WetSeason,
    Undefined
  )

  case object Winter    extends Season
  case object Spring    extends Season
  case object Summer    extends Season
  case object Autumn    extends Season
  case object DrySeason extends Season
  case object WetSeason extends Season
  case object Undefined extends Season

    def toSeason(s: String): Season = {
    s.toLowerCase match {
      case "winter"     => Winter
      case "spring"     => Spring
      case "summer"     => Summer
      case "autumn"     => Autumn
      case "dry_season" => DrySeason
      case "wet_season" => WetSeason
      case _            => Undefined
    }
  }
}
