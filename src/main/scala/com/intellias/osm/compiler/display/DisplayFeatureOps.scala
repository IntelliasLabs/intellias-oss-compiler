package com.intellias.osm.compiler.display

import com.intellias.osm.compiler.display.conf.DisplayFeatureProperties
import com.intellias.osm.model.display.DisplayFeatureType
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

trait DisplayFeatureOps {
  val knownFeaturesProperties: Array[DisplayFeatureProperties]

  def isSupportedFeatureType(tags: Map[String, String]): Boolean = knownFeaturesProperties.findMatched(tags).nonEmpty

  val classifyFeatureUdf: UserDefinedFunction = udf((tags: Map[String, String]) => {
    knownFeaturesProperties
      .findMatched(tags)
      .map(_.featureTypeMapped)
      .getOrElse(DisplayFeatureType.Unknown)
      .name
  })
}
