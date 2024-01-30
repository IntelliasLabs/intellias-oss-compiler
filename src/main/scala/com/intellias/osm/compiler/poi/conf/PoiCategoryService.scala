package com.intellias.osm.compiler.poi.conf

import com.intellias.osm.PoiConfig
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.poi.POICategory

trait PoiCategoryService {
  val categoryService: PoiCategoryService.Service
}

object PoiCategoryService {
  trait Service extends Serializable {
    def all: Seq[POICategory]
    def categoriesByIds(categoryIds: Set[Int]): Seq[POICategory]
  }

  def apply(poiConf: PoiConfig, langService: LanguageService.Service): PoiCategoryService.Service =
    PoiCategoryServiceImpl(poiConf, langService)
}

class PoiCategoryServiceImpl(categories: Seq[POICategory]) extends PoiCategoryService.Service {
  override def all: Seq[POICategory] = categories
  override def categoriesByIds(categoryIds: Set[Int]): Seq[POICategory] = categories.filter(c => categoryIds(c.categoryId))
}

object PoiCategoryServiceImpl {
  def apply(poiConf: PoiConfig, langService: LanguageService.Service): PoiCategoryServiceImpl =
    new PoiCategoryServiceImpl(PoiCategoryConfigBuilder.buildCategories(poiConf, langService))
}
