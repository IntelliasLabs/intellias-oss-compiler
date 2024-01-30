package com.intellias.osm.compiler.poi.attributes.keys

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey
import com.intellias.osm.model.poi.SocketType

sealed abstract class OsmSocketType(val values: String, val socketType: SocketType) {
  override def toString: String = values
}

//TODO: add more sockets types (https://wiki.openstreetmap.org/wiki/Key:socket)
object OsmSocketType extends OsmCompositeKey[OsmSocketType] {
  override val values: Seq[OsmSocketType] = Seq(
    IEC62196_2_T2O,
    COMBO_T2,
    DC_CHADEMO,
    DOMESTIC_F,
    IEC62196_2_T1C
  )

  override def groupName: String = "evSocketType"
  override def default: OsmSocketType = IEC62196_2_T2O

  final case object IEC62196_2_T2O extends OsmSocketType("type2", SocketType.IEC62196_2_T2O)
  final case object COMBO_T2 extends OsmSocketType("type2_combo", SocketType.COMBO_T2)
  final case object DC_CHADEMO extends OsmSocketType("chademo", SocketType.DC_CHADEMO)
  final case object DOMESTIC_F extends OsmSocketType("schuko", SocketType.DOMESTIC_F)
  final case object IEC62196_2_T1C extends OsmSocketType("type1", SocketType.IEC62196_2_T1C)

}
