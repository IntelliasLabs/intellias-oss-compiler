package com.intellias.osm.ndslive.common

import com.intellias.osm.model.common.{DirectionType, Side}

trait DirectionDetector {
  //TODO: implement side detection based on region rules when logic with assignment road to the region will be implemented.
  def sideToDirection(side: Side): DirectionType = side match {
    case Side.Both => DirectionType.Both
    case Side.Left => DirectionType.Backward
    case Side.Right => DirectionType.Forward
  }
}
