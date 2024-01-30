package com.intellias.osm.compiler.attributes

import play.api.libs.json.Writes

import scala.language.implicitConversions

trait AttributeExtractorLike[FO] extends Serializable {
  type T

  implicit def writes: Writes[Seq[T]]

  def tag: String //TODO: move this tag to somewhere else???

  def decodeFromOsm(feature: FO): Seq[T]
}
