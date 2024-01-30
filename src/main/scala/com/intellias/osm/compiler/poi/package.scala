package com.intellias.osm.compiler

import com.intellias.osm._
import com.intellias.osm.common.dsl.TagsPredicate
import com.intellias.osm.common.{SourceType, StorageService}
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.language.{LanguageConfig, LanguageService}
import com.intellias.osm.compiler.poi.conf.PoiCategoryService
import com.intellias.osm.compiler.scale.{ScaleConfig, ScaleService}
import com.intellias.osm.model.poi.{POI, PoiStandardCategory, PoiTile}

package object poi {
  trait PoiEnvironment extends StorageService with LanguageService with ScaleService with PoiCategoryService with AdminPlaceService  with Serializable

  case class PoiWorkflowConfig(common: CommonConfig,
                               osm: OsmConfig,
                               nds: NdsLiveConfig,
                               poi: PoiConfig,
                               ndsStorage: StorageConfig,
                               scaleConfig: ScaleConfig,
                               langConfig: LanguageConfig,
                               adminPlace: AdminPlaceConfig)

  case class POICategory(categoryId: Int,
                         standardCategory: PoiStandardCategory.Value,
                         categoryNames: List[POICategoryName],
                         selectionEntry: Boolean,
                         showInSearchTree: Boolean,
                         isBrand: Boolean,
                         isCollective: Boolean,
                         iconSetReference: BigInt,
                         scaleRangeIds: List[Int],
                         activationRadius: Int,
                         parents: List[Int],
                         children: List[Int],
                         predicate: Option[TagsPredicate])

  case class POICategoryName(name: String, languageId: Short)

  case object PoiTable extends SourceType[POI]
  case object OverturePoiTable extends SourceType[POI]
  case object OpenChargeMapPoiTable extends SourceType[POI]
  case object NdsPoiTable extends SourceType[PoiTile]
}
