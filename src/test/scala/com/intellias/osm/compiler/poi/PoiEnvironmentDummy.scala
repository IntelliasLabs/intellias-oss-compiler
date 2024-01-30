package com.intellias.osm.compiler.poi

import com.intellias.osm.common.StorageService
import com.intellias.osm.compiler.DummyEnvironment
import com.intellias.osm.compiler.DummyServices._
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.poi.conf.PoiCategoryService
import com.intellias.osm.compiler.scale.ScaleService
import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.poi.POI

trait PoiEnvironmentDummy extends DummyEnvironment {

  val poiEnv: PoiEnvironment = new PoiEnvironment {
    override val scaleService: ScaleService.Service     = dummyEnv.scaleService
    override val storageServices: Seq[StorageService.Service] = dummyEnv.storageServices
    override val langService: LanguageService.Service = dummyEnv.langService
    override val adminPlaceService: AdminPlaceService.Service = dummyEnv.adminPlaceService
    override val categoryService: PoiCategoryService.Service  = PoiCategoryDummyService()
  }

  def creatDummyPoi(
      poiId: String = "osm-node-12",
      longitude: Double = 46.1,
      latitude: Double = 6.5,
      tileId: Int = 545366801,
      poiTags: Map[String, String],
      ndsId: Int = 1,
      adminPlace: Option[FeatureAdminPlace] = dummyAdminPlace): POI = {
    POI(poiId, longitude, latitude, tileId, poiTags, ndsId = ndsId,  adminPlace = adminPlace)
  }

  def creatDummyPoi(tags: Map[String, String]): POI = creatDummyPoi(poiTags = tags)
}
