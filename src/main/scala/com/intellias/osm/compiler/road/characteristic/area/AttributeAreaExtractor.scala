package com.intellias.osm.compiler.road.characteristic.area

trait AttributeAreaExtractor[A, FO] extends AttributeAreaExtractorLike[FO] with Serializable {
  type T = A
}