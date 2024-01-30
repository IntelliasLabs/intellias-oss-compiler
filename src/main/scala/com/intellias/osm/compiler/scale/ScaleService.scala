package com.intellias.osm.compiler.scale

import com.intellias.osm.AppConfig
import pureconfig.generic.auto._

trait ScaleService {
  val scaleService: ScaleService.Service
}
object ScaleService {
  trait Service extends Serializable {
    def scaleRanges: Seq[Scale]
  }

  def apply(scaleConfig: ScaleConfig): ScaleService = new ScaleService {
    override val scaleService: Service = ScaleServiceImpl(scaleConfig)
  }
}

class ScaleServiceImpl(scales: Seq[Scale]) extends ScaleService.Service {
  override def scaleRanges: Seq[Scale] = scales
}
object ScaleServiceImpl {
  case class ScaleRangeMapping(scaleRanges: Seq[Scale])

  def apply(scalaConf: ScaleConfig): ScaleServiceImpl = new ScaleServiceImpl(
    AppConfig.read[ScaleRangeMapping](Some(scalaConf.scalePath)).scaleRanges
  )
}
