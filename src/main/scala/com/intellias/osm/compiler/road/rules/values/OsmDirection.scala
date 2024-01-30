package com.intellias.osm.compiler.road.rules.values

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey
import com.intellias.osm.model.common.DirectionType

// https://wiki.openstreetmap.org/wiki/Forward_%26_backward,_left_%26_right
sealed abstract class OsmDirection(val value: String, val direction: DirectionType) {
  override def toString: String = value
}

case object OsmDirection extends OsmCompositeKey[OsmDirection] {

  override val values: Seq[OsmDirection] = Seq(Both, Forward, Backward)

  override def groupName: String = "direction"

  override def default: OsmDirection = Both

  final case object Both extends OsmDirection("", DirectionType.Both)

  final case object Forward extends OsmDirection("forward", DirectionType.Forward)

  final case object Backward extends OsmDirection("backward", DirectionType.Backward)

  def toDirection(value: String): OsmDirection = {
    value match {
      case "forward"  => Forward
      case "backward" => Backward
      case ""         => Both
      case _          => throw new IllegalArgumentException(s"OsmDirection of type $value does not expected")
    }
  }

}
