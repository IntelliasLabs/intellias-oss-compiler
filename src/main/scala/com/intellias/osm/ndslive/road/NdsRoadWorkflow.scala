package com.intellias.osm.ndslive.road

import com.intellias.osm.AppConfig
import com.intellias.osm.common.{Processor, StorageService, Workflow}
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.road.{RoadEnvironment, RoadTileReader, RoadWorkflowConfig}
import com.typesafe.scalalogging.StrictLogging
import pureconfig.generic.auto._


class NdsRoadWorkflow(val appConfig: RoadWorkflowConfig, env: StorageService with LanguageService) extends Workflow with StrictLogging {

  val processors: Seq[Processor] = Seq(
    RoadTileReader(appConfig.road),
    NdsLiveRoadIntersectionWriter(appConfig.nds, env),
    NdsLiveRoadCharacteristicsWriter(appConfig.nds, env),
    NdsLiveRoadRulesWriter(appConfig.nds, env),
    NdsLiveRoadRulesMetadataWriter(appConfig.nds, env),
    NdsLiveRoadCharacteristicsMetadataWriter(appConfig.nds, env, appConfig.road)
  )
}

object NdsRoadWorkflow {
  def apply(appConfigFile: Option[String]): NdsRoadWorkflow = {
    val appConfig: RoadWorkflowConfig = AppConfig.read[RoadWorkflowConfig](appConfigFile)
    val env: RoadEnvironment = new RoadEnvironment {
      override val storageServices: Seq[StorageService.Service] = StorageService(appConfig.ndsStorage).storageServices
      override val adminPlaceService: AdminPlaceService.Service = AdminPlaceService(appConfig.adminPlace).adminPlaceService
      override val langService: LanguageService.Service = LanguageService(appConfig.langConfig).langService
    }

    new NdsRoadWorkflow(appConfig, env)
  }
}


