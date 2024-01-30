package com.intellias.osm.compiler.attributes

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.flag

package object keys {

  val ConditionalSeparator: String = "@"

  val ValuesSeparator: String = ";"

  val ConditionalKey: OsmCompositeKey[Boolean] = flag("conditional")
}
