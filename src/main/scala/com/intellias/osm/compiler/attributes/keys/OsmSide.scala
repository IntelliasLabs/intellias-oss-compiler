package com.intellias.osm.compiler.attributes.keys

import com.intellias.osm.model.common.Side

abstract class OsmSide(val values: String, val side: Side) {
  override def toString: String = values
}

object OsmSide extends OsmCompositeKey[OsmSide] {
  override val values: Seq[OsmSide] = Seq(Left, Right, Both)
  override val extraValuesForReaders: Seq[OsmSide] = Seq(Both)

  override def groupName: String = "side"

  override def default: OsmSide = Both

  case object Both extends OsmSide("both", Side.Both)
  case object Left extends OsmSide("left", Side.Left)
  case object Right extends OsmSide("right", Side.Right)
}
