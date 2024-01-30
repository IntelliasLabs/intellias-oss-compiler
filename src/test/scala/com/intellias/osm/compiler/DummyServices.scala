package com.intellias.osm.compiler

import com.intellias.osm.common.StorageService
import com.intellias.osm.common.StorageService.{Key, Payload}
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.display.conf.{DisplayFeaturePropertiesList, DisplayPropertiesService}
import com.intellias.osm.compiler.language.{Language, LanguageService}
import com.intellias.osm.compiler.poi.conf.PoiCategoryService
import com.intellias.osm.compiler.scale.ScaleService
import com.intellias.osm.model.admin.{AdminPlaceGround, FeatureAdminPlace}

object DummyServices {
  case class ScaleDummyService() extends ScaleService.Service {
    override def scaleRanges: Seq[scale.Scale] = Seq.empty
  }

  case class StorageDummyService() extends StorageService.Service {
    override def save(key: Key, payload: Payload): Unit = ()
    override def save(payloads: Iterable[(Key, Payload)]): Unit = ()
    override def saveFailure(key: Key, payload: Payload): Unit = ()
    override def saveFailure(payloads: Iterable[(Key, Payload)]): Unit = ()
  }

  case class LanguageDummyService() extends LanguageService.Service {
    override def all: Seq[Language] = Seq.empty
    override def countryDefault(isoCountry: String): Option[Language] = None
    override def countryOfficials(isoCountry: String): Seq[Language] = Seq.empty
    override def regionDefault(isoCountry: String, isoSubrb: List[String]): Option[Language] = None
    override def regionOfficials(isoCountry: String, isoSubrb: String): Seq[Language] = Seq.empty
    override def globalDefault(isoLanguage: String): Option[Language] = None
    override def defaultLanguage: Language = Language(1, "USA", "eng", "Latn", None, None, isCountryDefault = true, isGlobal = true, Seq.empty )

    override def getCountryOrGlobal(isoCountry: String, isoLanguage: String): Option[Language] = Some(defaultLanguage)
  }

  case class PoiCategoryDummyService() extends PoiCategoryService.Service {
    override def all: Seq[poi.POICategory] = Seq.empty
    override def categoriesByIds(categoryIds: Set[Int]): Seq[poi.POICategory] = Seq.empty
  }

  case class AdminPlaceDummyService() extends AdminPlaceService.Service {
    override def resolveFeatureAdminPlace(adminPlaces: List[AdminPlaceGround]): Option[FeatureAdminPlace] = None

    override def resolveFeatureAdminPlaces(adminPlaces: List[AdminPlaceGround]): List[FeatureAdminPlace] = List.empty[FeatureAdminPlace]
  }

  case class DisplayPropertiesDummyService() extends DisplayPropertiesService.Service {
    override def displayPropertiesList: DisplayFeaturePropertiesList = DisplayFeaturePropertiesList(
      Array.empty, Array.empty, Array.empty
    )
  }

}
