package com.intellias.osm.model.admin

import com.intellias.osm.model.common.wrapper.MultiPolygonWrapper
import play.api.libs.json.{Format, Json}

case class AdminPlace(adminPlaceId: String,
                      areaId: Long,
                      isoCountryCode: String,
                      isoSubCountryCode: List[String],
                      parents: List[AdminPlaceRef],
                      adminLevel: Int,
                      adminType: AdminTypeWrapper,
                      tags: Map[String, String],
                      area: MultiPolygonWrapper,
                      coveredTiles: Set[Long])

object AdminPlace {
  implicit val adminPlaceFormat: Format[AdminPlace] = Json.format[AdminPlace]
}