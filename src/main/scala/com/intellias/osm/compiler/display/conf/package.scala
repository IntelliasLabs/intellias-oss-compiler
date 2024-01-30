package com.intellias.osm.compiler.display

import com.intellias.osm.common.dsl.{PredicateConf, TagsPredicate}
import com.intellias.osm.model.display.DisplayFeatureType

package object conf {
  case class DisplayFeatureProperties(featureType: String,
                                      drawingOrder: Int,
                                      scaleLevels: Array[Int],
                                      predicate: Option[PredicateConf]) {
    lazy val featureTypeMapped: DisplayFeatureType = DisplayFeatureType.lookup(featureType)
    lazy val tagsPredicate: Option[TagsPredicate] = predicate.map(TagsPredicate(_))
  }

  case class DisplayFeaturePropertiesList(areas: Array[DisplayFeatureProperties],
                                          lines: Array[DisplayFeatureProperties],
                                          points: Array[DisplayFeatureProperties])

  case class DisplayFeatureConfig(configPath: String)

  implicit class DisplayPropertiesOps(featureProps: Array[DisplayFeatureProperties]) {
    def findMatched(lookupTags: Map[String, String]): Option[DisplayFeatureProperties] = {
      featureProps.find(_.tagsPredicate.exists(_.isMatched(lookupTags)))
    }
  }
}
