package com.intellias.osm.compiler.attributes.keys

import com.intellias.osm.model.common.Side

abstract class OsmRoadSide(val values: String, val side: Side) {
  override def toString: String = values
}

object OsmRoadSide extends OsmCompositeKey[OsmRoadSide] {
  override val values: Seq[OsmRoadSide] = Seq(Left, Right, Both)
  override def groupName: String        = "roadside"

  override def default: OsmRoadSide = Both

  case object Both  extends OsmRoadSide("both", Side.Both)
  case object Left  extends OsmRoadSide("left", Side.Left)
  case object Right extends OsmRoadSide("right", Side.Right)

  def toSide(value: String): OsmRoadSide = {
    value match {
      case "both"  => Both
      case "left"  => Left
      case "right" => Right
      case _       => throw new IllegalArgumentException(s"OSM side of type $value does not expected")
    }
  }
}
