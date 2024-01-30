package com.intellias.osm.compiler.road.rules

import com.intellias.osm.compiler.attributes.AttributeExtractor
import com.intellias.osm.model.road.Topology

trait RoadRulesExtractor[T] extends AttributeExtractor[T, Topology] with Serializable {
  type A = T
}