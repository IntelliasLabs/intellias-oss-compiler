package com.intellias.osm.compiler.poi

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.model.poi.POI
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.{Dataset, SparkSession}

case class PoiCategoryProcessor(commonConf: CommonConfig, env: PoiEnvironment) extends Processor with StrictLogging {

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    data + (PoiTable -> addCategories(data(PoiTable)))
  }

  private def addCategories(pois: Dataset[POI])(implicit spark: SparkSession): Dataset[POI] = {
    import spark.implicits._
    pois
      .map(assignCategoriesToPoi)
      .filter(_.categories.nonEmpty)
      .persist(commonConf.sparkStorageLevel)
  }

  private def assignCategoriesToPoi(poi: POI): POI = {
    poi.copy(categories = poi.categories ++ env.categoryService.all.collect {
      case c: POICategory if c.predicate.exists(_.isMatched(poi.tags)) => c.categoryId
    })
  }

}
