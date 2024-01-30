package com.intellias.osm.model.display

import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.common.wrapper.MultiPolygonWrapper

case class DisplayArea(tileId: Int,
                       localId: Int,
                       originalId: String,
                       geometry: MultiPolygonWrapper,
                       featureType: String,
                       tags: Map[String, String] = Map.empty,
                       adminPlaces: List[FeatureAdminPlace] = List.empty)
