package com.intellias.osm.compiler.road

import com.intellias.osm.AppConfig
import com.intellias.osm.common.{Processor, StorageService, Workflow}
import com.intellias.osm.compiler.admin.{AdminHierarchyReader, AdminPlaceService}
import com.intellias.osm.compiler.datasource.osm.OsmSourceReader
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.topology.{TopologyAdminTileProcessor, TopologyProcessor}
import com.typesafe.scalalogging.StrictLogging
import pureconfig.generic.auto._


class RoadWorkflow(val appConfig: RoadWorkflowConfig, env: RoadEnvironment) extends Workflow with StrictLogging {


  val processors: Seq[Processor] = Seq(
    OsmSourceReader(appConfig.common, appConfig.osm),
    AdminHierarchyReader(appConfig.adminPlace),
    TopologyProcessor(appConfig.common),
    TopologyAdminTileProcessor(appConfig.common, env),
    RoadAreaCharacteristicsProcessor(appConfig.common),
    RoadCharacteristicsProcessor(appConfig.common, env),
    RoadRulesProcessor,
    RoadRulesProcessorWithRelations,
    RoadIntersectionProcessor(appConfig.common),
    RoadTileWriter(appConfig.road),
    RoadStatisticsWriter(appConfig.road)
  )
}

object RoadWorkflow {
  def apply(appConfigFile: Option[String]): RoadWorkflow = {
    val appConfig: RoadWorkflowConfig = AppConfig.read[RoadWorkflowConfig](appConfigFile)
    val env: RoadEnvironment = new RoadEnvironment {
      override val storageServices: Seq[StorageService.Service] = StorageService(appConfig.ndsStorage).storageServices
      override val adminPlaceService: AdminPlaceService.Service = AdminPlaceService(appConfig.adminPlace).adminPlaceService
      override val langService: LanguageService.Service = LanguageService(appConfig.langConfig).langService
    }

    new RoadWorkflow(appConfig, env)
  }
}


