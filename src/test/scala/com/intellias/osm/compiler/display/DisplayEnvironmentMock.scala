package com.intellias.osm.compiler.display

import com.intellias.osm.common.StorageService
import com.intellias.osm.compiler.DummyEnvironment
import com.intellias.osm.compiler.DummyServices.{DisplayPropertiesDummyService, ScaleDummyService, StorageDummyService}
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.display.conf.DisplayPropertiesService
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.scale.ScaleService


trait DisplayEnvironmentMock extends DummyEnvironment {
  val env: DisplayEnvironment = new DisplayEnvironment {
    override val storageServices: Seq[StorageService.Service] = dummyEnv.storageServices
    override val scaleService: ScaleService.Service = dummyEnv.scaleService
    override val displayPropertiesService: DisplayPropertiesService.Service = DisplayPropertiesDummyService()
    override val langService: LanguageService.Service = dummyEnv.langService
    override val adminPlaceService: AdminPlaceService.Service = dummyEnv.adminPlaceService
  }
}
