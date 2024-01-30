package com.intellias.osm.ndslive.name.display

import nds.name.instantiations.NameDisplayPointAttribute

package object point {
  type DisplayPointNameAttr = DisplayFeatureNameAttr[NameDisplayPointAttribute]
  type PointNameAttrComponents = NameAttrComponents[NameDisplayPointAttribute]
  type PointNameAttrWithFeatures = DisplayNameAttrWithFeatures[NameDisplayPointAttribute]
}
