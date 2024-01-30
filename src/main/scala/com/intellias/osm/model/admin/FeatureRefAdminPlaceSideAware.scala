package com.intellias.osm.model.admin

case class FeatureRefAdminPlaceSideAware(featureTileId: Int,
                                         featureLocalId: Int,
                                         leftAdminPlaces: List[AdminPlaceGround],
                                         rightAdminPlaces: List[AdminPlaceGround])
