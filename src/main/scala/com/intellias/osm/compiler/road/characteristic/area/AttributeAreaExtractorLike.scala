package com.intellias.osm.compiler.road.characteristic.area

import com.intellias.osm.compiler.road.FeatureArea
import play.api.libs.json.Writes

trait AttributeAreaExtractorLike[FO] extends Serializable {
  type T

  implicit def writes: Writes[Seq[T]]

  def tag: String //TODO: move this tag to somewhere else???

  def decodeFromOsm(feature: FO, areas: Seq[FeatureArea]): Seq[T]
}