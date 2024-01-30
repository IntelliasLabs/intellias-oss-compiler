package com.intellias.osm.ndslive

import com.intellias.osm.common.StorageService
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.language.{LanguageConfig, LanguageService}
import com.intellias.osm.compiler.poi.conf.PoiCategoryService
import com.intellias.osm._

package object name {
  trait NdsNameEnvironment extends StorageService with LanguageService with PoiCategoryService with AdminPlaceService with Serializable

  case class NdsNameWorkflowConfig(common: CommonConfig,
                                   nds: NdsLiveConfig,
                                   poi: PoiConfig,
                                   road: RoadConfig,
                                   displayConfig: DisplayConfig,
                                   ndsStorage: StorageConfig,
                                   langConfig: LanguageConfig,
                                   adminPlace: AdminPlaceConfig)

}
