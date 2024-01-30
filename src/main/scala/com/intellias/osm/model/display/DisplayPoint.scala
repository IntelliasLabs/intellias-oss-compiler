package com.intellias.osm.model.display

import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.common.wrapper.CoordinateWrapper

case class DisplayPoint(tileId: Int,
                        localId: Int,
                        originalId: String,
                        geometry: CoordinateWrapper,
                        featureType: String,
                        tags: Map[String, String] = Map.empty,
                        adminPlace: Option[FeatureAdminPlace])
