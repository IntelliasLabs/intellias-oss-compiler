package com.intellias.osm.compiler.display

import com.intellias.osm.common.{Processor, StorageService, Workflow}
import com.intellias.osm.compiler.admin.{AdminHierarchyReader, AdminPlaceService}
import com.intellias.osm.compiler.datasource.osm.OsmSourceReader
import com.intellias.osm.compiler.display.conf.DisplayPropertiesService
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.scale.ScaleService
import com.intellias.osm.{AppConfig, DisplayWorkflowConfig}
import com.typesafe.scalalogging.StrictLogging
import pureconfig.generic.auto._

class DisplayWorkflow(val appConfig: DisplayWorkflowConfig, env: DisplayEnvironment) extends Workflow with StrictLogging {
  override def processors: Seq[Processor] = Seq(
    OsmSourceReader(appConfig.common, appConfig.osm),
    AdminHierarchyReader(appConfig.adminPlace),
    BuildingFootprintsProcessor(appConfig.common),
    DisplayAreasProcessor(appConfig.common, env),
    DisplayAreasLocalIdGenerator(appConfig.common),
    BuildingFootprintsAdminProcessor(env),
    BuildingAttributesProcessor(appConfig.common, env),
    DisplayAreasAdminProcessor(env),
    DisplayAreasAttributesProcessor(appConfig.common, env),
    DisplayLinesProcessor(appConfig.common, env),
    DisplayLinesAdminProcessor(env),
    DisplayLinesAttributesProcessor(appConfig.common, env),
    DisplayPointsProcessor(appConfig.common, env),
    DisplayPointsAdminProcessor(env),
    DisplayPointAttributesProcessor(appConfig.common, env),
    DisplayTilesCollector(appConfig.common),
    DisplayTilesWriter(appConfig.displayConfig)
  )
}

object DisplayWorkflow {
  def apply(configFile: Option[String]): DisplayWorkflow = {
    val displayConfig = AppConfig.read[DisplayWorkflowConfig](configFile)
    val env = new DisplayEnvironment {
      override val storageServices: Seq[StorageService.Service] = StorageService(displayConfig.ndsStorage).storageServices
      override val scaleService: ScaleService.Service = ScaleService(displayConfig.scaleConfig).scaleService
      override val displayPropertiesService: DisplayPropertiesService.Service =
        DisplayPropertiesService(displayConfig.displayFeatureConfig).displayPropertiesService
      override val adminPlaceService: AdminPlaceService.Service = AdminPlaceService(displayConfig.adminPlace).adminPlaceService
      override val langService: LanguageService.Service = LanguageService(displayConfig.langConfig).langService
    }
    new DisplayWorkflow(displayConfig, env)
  }
}