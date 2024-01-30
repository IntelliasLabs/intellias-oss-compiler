package com.intellias.osm.model.poi

import com.intellias.osm.model.admin.FeatureAdminPlace

case class POI(poiId: String,
               longitude: Double,
               latitude: Double,
               tileId: Int,
               tags: Map[String, String] = Map.empty,
               ndsId: Int = -1,
               categories: Array[Int] = Array.empty,
               children: Array[POIRelation] = Array.empty,
               parents: Array[POIRelation] = Array.empty,
               adminPlace: Option[FeatureAdminPlace] = None)



