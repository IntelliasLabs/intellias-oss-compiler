package com.intellias.osm.model.common

case class FeatureRef[T](tileId: Int, localId: Int, geometry: T)