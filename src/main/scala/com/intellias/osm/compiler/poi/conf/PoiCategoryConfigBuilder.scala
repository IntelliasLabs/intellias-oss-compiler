package com.intellias.osm.compiler.poi.conf

import com.intellias.osm.common.dsl.{PredicateConf, TagsPredicate}
import com.intellias.osm.{AppConfig, PoiConfig}
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.poi
import com.intellias.osm.compiler.poi.{POICategory, POICategoryName}
import com.intellias.osm.model.poi.PoiStandardCategory
import pureconfig.generic.auto._

object PoiCategoryConfigBuilder {

  def buildCategories(poiConf: PoiConfig, langService: LanguageService.Service): Seq[POICategory] = {
    AppConfig.read[PoiCategoriesConf](Some(poiConf.categoryPath)).categories.map { catConf =>
      POICategory(
        categoryId = catConf.categoryId,
        standardCategory = PoiStandardCategory.valueOf(catConf.standardCategory),
        categoryNames = toNames(catConf.categoryNames, langService),
        selectionEntry = catConf.selectionEntry,
        showInSearchTree = catConf.showInSearchTree,
        isBrand = catConf.isBrand,
        isCollective = catConf.isCollective,
        iconSetReference = catConf.iconSetReference,
        scaleRangeIds = catConf.scaleRangeIds,
        activationRadius = catConf.activationRadius,
        parents = catConf.parents,
        children = catConf.children,
        predicate = catConf.predicate.map(TagsPredicate(_))
      )
    }
  }

  def toNames(categoryNames: List[CategoryNameConf], langService: LanguageService.Service): List[poi.POICategoryName] = {
    categoryNames.map { nameConf =>
      POICategoryName(
        name = nameConf.name,
        languageId = langService
          .globalDefault(nameConf.langCode)
          .getOrElse(throw new IllegalStateException(
            s"The category name '${nameConf.name}' references a language '${nameConf.langCode}' that could not be found in the LanguageService."))
          .langId
      )
    }
  }
}

case class PoiCategoriesConf(categories: Seq[PoiCategoryConf])
case class PoiCategoryConf(categoryId: Int,
                           standardCategory: String,
                           categoryNames: List[CategoryNameConf],
                           selectionEntry: Boolean,
                           showInSearchTree: Boolean,
                           isBrand: Boolean,
                           isCollective: Boolean,
                           iconSetReference: Short,
                           scaleRangeIds: List[Int],
                           activationRadius: Int,
                           parents: List[Int],
                           children: List[Int],
                           predicate: Option[PredicateConf])
case class CategoryNameConf(name: String, langCode: String)