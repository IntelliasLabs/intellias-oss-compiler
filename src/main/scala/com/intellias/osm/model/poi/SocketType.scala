package com.intellias.osm.model.poi

import com.intellias.osm.model.JsonFormatter

sealed abstract class SocketType(val current: ElectricCurrentType)

object SocketType extends JsonFormatter[SocketType] {
  val values: Seq[SocketType] = Seq(
    IEC62196_2_T2O,
    COMBO_T2,
    DC_CHADEMO,
    DOMESTIC_F,
    IEC62196_2_T1C
  )

  final case object IEC62196_2_T2O extends SocketType(ElectricCurrentType.DC)
  final case object COMBO_T2 extends SocketType(ElectricCurrentType.DC)
  final case object DC_CHADEMO extends SocketType(ElectricCurrentType.DC)
  final case object DOMESTIC_F extends SocketType(ElectricCurrentType.AC)
  final case object IEC62196_2_T1C extends SocketType(ElectricCurrentType.AC)
}