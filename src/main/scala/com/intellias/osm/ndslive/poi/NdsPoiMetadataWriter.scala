package com.intellias.osm.ndslive.poi

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{Processor, SharedProcessorData, StorageService}
import com.intellias.osm.compiler.language.{Language, LanguageService}
import com.intellias.osm.compiler.poi._
import com.intellias.osm.compiler.poi.conf.PoiCategoryService
import com.intellias.osm.compiler.scale.{Scale, ScaleService}
import com.intellias.osm.model.poi.PoiStandardCategory
import com.intellias.osm.ndslive.tools.NdsLiveTools.ConfigurableMetaConverters._
import nds.core.language.{AvailableLanguages, LanguageMapping, LanguageName}
import nds.core.types.ScaleRangeList
import nds.poi.metadata.{PoiCategory, PoiLayerMetadata}
import nds.poi.reference.types.{PoiStandardCategory => NdsStandartCategory}
import nds.poi.types.{NameString, NameStringCollection, NameStringRelationType, NameStringUsageType}
import org.apache.spark.sql.SparkSession
import zserio.runtime.io.SerializeUtil

case class NdsPoiMetadataWriter(ndsConf: NdsLiveConfig, env: StorageService with ScaleService with PoiCategoryService with LanguageService) extends Processor {

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    val layerMeta = new PoiLayerMetadata()
    layerMeta.setScales(buildScales(env.scaleService.scaleRanges))
    layerMeta.setCategories(buildCategories(env.categoryService.all))
    layerMeta.setAvailableLanguages(buildLanguages(env.langService.all))

    env.storageServices.foreach(_.save(Iterable((ndsConf.poiMetadata, SerializeUtil.serializeToBytes(layerMeta)))))

    data
  }

  private def buildCategories(categories: Iterable[POICategory]): Array[PoiCategory] = {
    categories.map{ cat =>
      new PoiCategory(
        cat.categoryId,
        toNdsStandardCategories(cat.standardCategory),
        new NameStringCollection(cat.categoryNames.map(toNameString).toArray),
        cat.selectionEntry,
        cat.showInSearchTree,
        cat.isBrand,
        cat.isCollective,
        cat.iconSetReference.bigInteger,
        cat.scaleRangeIds.toArray,
        cat.activationRadius,
        cat.parents.toArray,
        cat.children.toArray
      )
    }.toArray
  }

  private def toNameString(catName: POICategoryName): NameString = {
    val nameString = new NameString()
    nameString.setNameString(catName.name)
    nameString.setLanguageCode(catName.languageId)
    nameString.setNameStringRelationType(NameStringRelationType.NO_RELATION)
    nameString.setNameStringUsageType(NameStringUsageType.DEFAULT_OFFICIAL_NAME)

    nameString
  }


  private def buildScales(scales: Seq[Scale]): ScaleRangeList = new ScaleRangeList(scales.size, scales.toArray)

  private def buildLanguages(languages: Seq[Language]): AvailableLanguages = {
    new AvailableLanguages(languages.map(buildLanguage).toArray)
  }

  private def buildLanguage(l: Language):LanguageMapping = {
    val lm = new LanguageMapping()
    lm.setLanguageCode(l.langId)
    lm.setIsoCountryCode(l.isoCountryCode)
    lm.setIsoLanguageCode(l.isoLanguageCode)
    lm.setIsoScriptCode(l.isoScriptCode)
    l.isTransliterationOf.foreach(lm.setIsTransliterationOf)
    l.isDiacriticOf.foreach(lm.setIsDiacriticTransliterationOf)
    lm.setLanguageNames(l.languageNames.map(ln => new LanguageName(ln.id, ln.name)).toArray)

    lm
  }

  private def toNdsStandardCategories(standardCategory: PoiStandardCategory.Value): NdsStandartCategory = standardCategory match {
    case PoiStandardCategory.EvChargingStation => NdsStandartCategory.POICAT_EV_CHARGING_STATION
    case PoiStandardCategory.FillingStation => NdsStandartCategory.POICAT_FILLING_STATION
    case PoiStandardCategory.General => NdsStandartCategory.POICAT_NDSGENERAL
  }
}
