package com.intellias.osm.ndslive.poi

import com.intellias.osm.AppConfig
import com.intellias.osm.common.{Processor, StorageService, Workflow}
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.poi._
import com.intellias.osm.compiler.poi.conf.PoiCategoryService
import com.intellias.osm.compiler.scale.ScaleService
import com.typesafe.scalalogging.StrictLogging
import pureconfig.generic.auto._


class NdsPoiWorkflow(val appConfig: PoiWorkflowConfig, env: PoiEnvironment) extends Workflow with StrictLogging {

  val processors: Seq[Processor] = Seq(
    PoiTileReader(appConfig.poi),
    NdsPoiWriter(appConfig.nds, env),
    NdsPoiMetadataWriter(appConfig.nds, env),
    NdsPoiAttributeWriter(appConfig.nds, env),
    NdsPoiAttrMetadataWriter(appConfig.nds, env)
  )
}

object NdsPoiWorkflow {
  def apply(appConfigFile: Option[String]): NdsPoiWorkflow = {
    val appConfig: PoiWorkflowConfig = AppConfig.read[PoiWorkflowConfig](appConfigFile)
    val env: PoiEnvironment = new PoiEnvironment {
      override val langService: LanguageService.Service         = LanguageService(appConfig.langConfig).langService
      override val storageServices: Seq[StorageService.Service] = StorageService(appConfig.ndsStorage).storageServices
      override val scaleService: ScaleService.Service           = ScaleService(appConfig.scaleConfig).scaleService
      override val categoryService: PoiCategoryService.Service  = PoiCategoryService(appConfig.poi, langService)
      override val adminPlaceService: AdminPlaceService.Service = AdminPlaceService(appConfig.adminPlace).adminPlaceService
    }
    new NdsPoiWorkflow(appConfig, env)
  }
}
