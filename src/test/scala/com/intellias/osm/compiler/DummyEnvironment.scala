package com.intellias.osm.compiler

import com.intellias.osm.common.StorageService
import com.intellias.osm.compiler.DummyServices.{AdminPlaceDummyService, ScaleDummyService, StorageDummyService}
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.language.{LanguageConfig, LanguageService}
import com.intellias.osm.compiler.scale.ScaleService
import com.intellias.osm.model.admin.FeatureAdminPlace

trait DummyEnvironment {

  val dummyEnv: StorageService with LanguageService with ScaleService with AdminPlaceService with Serializable = new StorageService
  with LanguageService with ScaleService with AdminPlaceService with Serializable {
    override val scaleService: ScaleService.Service     = ScaleDummyService()
    override val storageServices: Seq[StorageService.Service] = Seq(StorageDummyService())
    override val langService: LanguageService.Service = LanguageService(
      LanguageConfig(langPath = "src/main/resources/mapping/lang/languages.conf",
                     countryPath = "src/main/resources/mapping/lang/iso3166_2_languages.csv")
    ).langService
    override val adminPlaceService: AdminPlaceService.Service = AdminPlaceDummyService()
  }

  def dummyAdminPlace: Option[FeatureAdminPlace] = Some(
    FeatureAdminPlace(adminPlaceId = "UA-12345678", isoCountryCode = "UKR", isoSubCountryCode = List("UA-46"), adminLevel = 8)
  )

  def getGlobalLanguage(langCode: String): Short = dummyEnv.langService.globalDefault(langCode).get.langId

}
