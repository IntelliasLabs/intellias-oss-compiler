package com.intellias.osm.compiler

package object scale {
  case class Scale(rangeId: Int, level: Byte, min: Int, max: Int)
  case class ScaleConfig(scalePath: String)
}
