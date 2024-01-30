package com.intellias.osm.compiler.poi

import com.intellias.osm.AppConfig
import com.intellias.osm.common.{Processor, StorageService, Workflow}
import com.intellias.osm.compiler.admin.{AdminHierarchyReader, AdminPlaceService}
import com.intellias.osm.compiler.datasource.osm.OsmSourceReader
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.poi.conf.PoiCategoryService
import com.intellias.osm.compiler.scale.ScaleService
import com.typesafe.scalalogging.StrictLogging
import pureconfig.generic.auto._


class PoiWorkflow(val appConfig: PoiWorkflowConfig, env: PoiEnvironment) extends Workflow with StrictLogging {

  val processors: Seq[Processor] = Seq(
    OsmSourceReader(appConfig.common, appConfig.osm),
    AdminHierarchyReader(appConfig.adminPlace),
    OsmPoiPreProcessor(appConfig.common),
    PoiCategoryProcessor(appConfig.common, env),
    PoiAdminPlaceProcessor(appConfig.common, env),
    PoiAttributeProcessor(appConfig.common, env),
    PoiPertTileProcessor(appConfig.common),
    PoiTileWriter(appConfig.poi),
  )
}

object PoiWorkflow {
  def apply(appConfigFile: Option[String]): PoiWorkflow = {
    val appConfig: PoiWorkflowConfig = AppConfig.read[PoiWorkflowConfig](appConfigFile)
    val env: PoiEnvironment = new PoiEnvironment {
      override val langService: LanguageService.Service = LanguageService(appConfig.langConfig).langService
      override val storageServices: Seq[ StorageService.Service] = StorageService(appConfig.ndsStorage).storageServices
      override val scaleService: ScaleService.Service = ScaleService(appConfig.scaleConfig).scaleService
      override val categoryService: PoiCategoryService.Service = PoiCategoryService(appConfig.poi, langService)
      override val adminPlaceService: AdminPlaceService.Service = AdminPlaceService(appConfig.adminPlace).adminPlaceService
    }
    new PoiWorkflow(appConfig, env)
  }
}


