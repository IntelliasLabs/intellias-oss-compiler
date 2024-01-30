package com.intellias.osm.ndslive.name.display

import nds.name.instantiations.NameDisplayAreaAttribute

package object area {
  type DisplayAreaNameAttr = DisplayFeatureNameAttr[NameDisplayAreaAttribute]
  type AreaNameAttrComponents = NameAttrComponents[NameDisplayAreaAttribute]
  type AreaNameAttrWithFeatures = DisplayNameAttrWithFeatures[NameDisplayAreaAttribute]
}
