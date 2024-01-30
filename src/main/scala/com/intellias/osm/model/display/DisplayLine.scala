package com.intellias.osm.model.display

import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.common.wrapper.LineWrapper

case class DisplayLine(tileId: Int,
                       localId: Int,
                       originalId: String,
                       geometry: LineWrapper,
                       featureType: String,
                       tags: Map[String, String] = Map.empty,
                       leftAdminPlaces: List[FeatureAdminPlace] = List.empty,
                       rightAdminPlaces: List[FeatureAdminPlace] = List.empty)
