package com.intellias.osm.compiler.poi

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.poi.attributes.PoiCatToAttrExtractors
import com.intellias.osm.model.poi.POI
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import play.api.libs.json.Json

case class PoiAttributeProcessor(commonConf: CommonConfig, poiEnv: PoiEnvironment) extends Processor with StrictLogging {
  private val catToExtractors = PoiCatToAttrExtractors(poiEnv)

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val ds = data(PoiTable)
      .map(populateAttributes)
      .persist(commonConf.sparkStorageLevel)

    data + (PoiTable -> ds)
  }

  private def populateAttributes(poi: POI): POI = {
    val attributes = catToExtractors
      .getExtractors(poiEnv.categoryService.categoriesByIds(poi.categories.toSet).map(c => c.standardCategory))
      .flatMap { extractor =>
        val attributes = extractor.decodeFromOsm(poi)
        if (attributes.nonEmpty) {
          val json = Json.toJson(attributes)(extractor.writes).toString()
          Some(extractor.tag -> json)
        } else None
      }

    poi.copy(tags = poi.tags ++ attributes)
  }

}
