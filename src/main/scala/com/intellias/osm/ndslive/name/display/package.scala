package com.intellias.osm.ndslive.name

import nds.core.attributemap.ConditionList
import nds.name.instantiations.NamePropertyList

package object display {
  case class NameAttrComponents[T](nameAttribute: T,
                                   properties: NamePropertyList,
                                   conditions: ConditionList)
  case class DisplayFeatureNameAttr[T](displayFeatureId: Int, nameAttrComponents: NameAttrComponents[T])
  case class DisplayNameAttrWithFeatures[T](nameAttrComponents: NameAttrComponents[T], features: Seq[Int])
}
