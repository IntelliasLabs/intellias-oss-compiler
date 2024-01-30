package com.intellias.osm.ndslive.display

import com.intellias.osm.{AppConfig, DisplayWorkflowConfig}
import com.intellias.osm.common.{Processor, StorageService, Workflow}
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.display.{DisplayEnvironment, DisplayTilesReader}
import com.intellias.osm.compiler.display.conf.DisplayPropertiesService
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.scale.ScaleService
import pureconfig.generic.auto._

class NdsDisplayWorkflow(val appConfig: DisplayWorkflowConfig, env: DisplayEnvironment) extends Workflow {
  override def processors: Seq[Processor] = Seq(
    DisplayTilesReader(appConfig.displayConfig),
    NdsDisplayWriter(appConfig.nds, appConfig.common, env),
    NdsDisplayMetadataWriter(appConfig.nds, env),
    NdsDisplayAttributeWriter(appConfig.nds, env),
    NdsDisplayAttributeMetadataWriter(appConfig.nds, env)
  )
}

object NdsDisplayWorkflow {
  def apply(configFile: Option[String]): NdsDisplayWorkflow = {
    val displayConfig = AppConfig.read[DisplayWorkflowConfig](configFile)
    val env = new DisplayEnvironment {
      override val storageServices: Seq[StorageService.Service] = StorageService(displayConfig.ndsStorage).storageServices
      override val scaleService: ScaleService.Service = ScaleService(displayConfig.scaleConfig).scaleService
      override val displayPropertiesService: DisplayPropertiesService.Service =
        DisplayPropertiesService(displayConfig.displayFeatureConfig).displayPropertiesService
      override val adminPlaceService: AdminPlaceService.Service = AdminPlaceService(displayConfig.adminPlace).adminPlaceService
      override val langService: LanguageService.Service = LanguageService(displayConfig.langConfig).langService
    }
    new NdsDisplayWorkflow(displayConfig, env)
  }
}