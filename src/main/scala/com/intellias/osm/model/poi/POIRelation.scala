package com.intellias.osm.model.poi

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate

case class POIRelation (
                         ndsId: Int,
                         tileId: Int,
                         position: Option[Wgs84Coordinate],
                         relationType: String)