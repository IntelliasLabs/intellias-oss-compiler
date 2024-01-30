package com.intellias.osm.ndslive.name

import com.intellias.osm.AppConfig
import com.intellias.osm.common.{Processor, StorageService, Workflow}
import com.intellias.osm.compiler.admin.{AdminHierarchyReader, AdminPlaceService}
import com.intellias.osm.compiler.display.DisplayTilesReader
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.poi._
import com.intellias.osm.compiler.poi.conf.PoiCategoryService
import com.intellias.osm.compiler.road.RoadTileReader
import com.intellias.osm.ndslive.name.display.{NdsDisplayNameMetadataWriter, NdsDisplayNameWriter}
import com.intellias.osm.ndslive.name.poi.{NdsPoiNameMetadataWriter, NdsPoiNameWriter}
import com.intellias.osm.ndslive.name.road.{NdsRoadNameMetadataWriter, NdsRoadNameWriter}
import com.typesafe.scalalogging.StrictLogging
import pureconfig.generic.auto._


class NdsNameWorkflow(val appConfig: NdsNameWorkflowConfig, env: NdsNameEnvironment) extends Workflow with StrictLogging {

  val processors: Seq[Processor] = Seq(
    PoiTileReader(appConfig.poi),
    AdminHierarchyReader(appConfig.adminPlace),
    RoadTileReader(appConfig.road),
    DisplayTilesReader(appConfig.displayConfig),

    NdsAdminTileProcessor(env),

    NdsPoiNameWriter(appConfig.nds, env),
    NdsPoiNameMetadataWriter(appConfig.nds, env),
    NdsRoadNameWriter(appConfig.nds, env, appConfig.common),
    NdsRoadNameMetadataWriter(appConfig.nds, env),
    NdsDisplayNameWriter(appConfig.nds, env),
    NdsDisplayNameMetadataWriter(appConfig.nds, env)
  )
}

object NdsNameWorkflow {
  def apply(appConfigFile: Option[String]): NdsNameWorkflow = {
    val appConfig: NdsNameWorkflowConfig = AppConfig.read[NdsNameWorkflowConfig](appConfigFile)
    val env: NdsNameEnvironment = new NdsNameEnvironment {
      override val langService: LanguageService.Service = LanguageService(appConfig.langConfig).langService
      override val storageServices: Seq[StorageService.Service] = StorageService(appConfig.ndsStorage).storageServices
      override val categoryService: PoiCategoryService.Service = PoiCategoryService(appConfig.poi, langService)
      override val adminPlaceService: AdminPlaceService.Service = AdminPlaceService(appConfig.adminPlace).adminPlaceService
    }
    new NdsNameWorkflow(appConfig, env)
  }
}


