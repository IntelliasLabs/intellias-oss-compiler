package com.intellias.osm.model.road

import com.intellias.osm.model.common.DirectionType
import play.api.libs.json.{Format, Json}

/**
 * Used for keys, which are massively present in OSM data, but has no explanation on wiki
 * Example: https://taginfo.openstreetmap.org/keys/stopping#values
 */
case class MarginalKeyType(key: String, value: String, direction: Option[DirectionType])

object MarginalKeyType {
  implicit val format: Format[MarginalKeyType] = Json.format[MarginalKeyType]
}
