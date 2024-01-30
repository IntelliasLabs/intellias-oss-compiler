package com.intellias.osm.ndslive.name.display

import nds.name.instantiations.NameDisplayLineRangeAttribute

package object line {
  type DisplayLineNameAttr = DisplayFeatureNameAttr[NameDisplayLineRangeAttribute]
  type LineNameAttrComponents = NameAttrComponents[NameDisplayLineRangeAttribute]
  type LineNameAttrWithFeatures = DisplayNameAttrWithFeatures[NameDisplayLineRangeAttribute]
}
