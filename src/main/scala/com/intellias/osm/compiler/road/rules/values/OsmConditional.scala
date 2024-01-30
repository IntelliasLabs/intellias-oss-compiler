package com.intellias.osm.compiler.road.rules.values

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey

// https://wiki.openstreetmap.org/wiki/Forward_%26_backward,_left_%26_right
sealed abstract class OsmConditional(val value: String) {
  override def toString: String = value
}

case object OsmConditional extends OsmCompositeKey[OsmConditional] {

  override val values: Seq[OsmConditional] = Seq(Conditional )

  override def groupName: String = "conditional"

  override def default: OsmConditional = Conditional

  final case object Conditional extends OsmConditional("conditional")


}
