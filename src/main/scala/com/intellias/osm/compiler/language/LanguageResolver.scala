package com.intellias.osm.compiler.language

import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.poi.POI

import scala.language.implicitConversions

trait LanguageResolver {

  def getRegionOrDefaultLang(langService: LanguageService.Service)(featureAdmin: Option[FeatureAdminPlace]): Language =
    adminPlaceToLanguage(langService)(featureAdmin)
      .getOrElse(langService.defaultLanguage)

  def getLanguage(langService: LanguageService.Service)(langCode: String, featureAdmin: Option[FeatureAdminPlace]): Option[Language] =
    featureAdmin.flatMap { ap =>
      langService
        .countryOfficials(ap.isoCountryCode)
        .find(cl => cl.isoLanguageCode == langCode)
    }.orElse(langService.globalDefault(langCode))


  def isOfficialLanguage(langService: LanguageService.Service)(langCode: String, featureAdmin: Option[FeatureAdminPlace]): Boolean =
    featureAdmin.exists(ap => langService.countryOfficials(ap.isoCountryCode).exists(l => l.isoLanguageCode == langCode))


  def adminPlaceToLanguage(langService: LanguageService.Service)(adminPlace: Option[FeatureAdminPlace]): Option[Language] =
    for {
      admin <- adminPlace
      lang <- langService.regionDefault(admin.isoCountryCode, admin.isoSubCountryCode)
    } yield lang

  implicit def poiToFeatureAdminPlace(poi: POI): Option[FeatureAdminPlace] = poi.adminPlace

}
