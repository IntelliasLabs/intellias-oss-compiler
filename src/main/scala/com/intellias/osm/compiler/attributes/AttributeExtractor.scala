package com.intellias.osm.compiler.attributes

trait AttributeExtractor[A, FO] extends AttributeExtractorLike[FO] with Serializable {
  type T = A
}