package com.intellias.osm.model.admin

case class FeatureAdminPlace(
                              adminPlaceId: String,
                              isoCountryCode: String,
                              isoSubCountryCode: List[String],
                              adminLevel: Int)