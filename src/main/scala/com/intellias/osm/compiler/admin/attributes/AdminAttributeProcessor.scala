package com.intellias.osm.compiler.admin.attributes

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.admin.{AdminEnvironment, AdminHierarchyTable}
import com.intellias.osm.compiler.attributes.AttributeExtractorLike
import com.intellias.osm.model.admin.AdminPlace
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import play.api.libs.json.Json

case class AdminAttributeProcessor(commonConf: CommonConfig, env: AdminEnvironment) extends Processor with StrictLogging {
  private val extractors: Seq[AttributeExtractorLike[AdminPlace]] = Seq(AdminNameExtractor(env))

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val ds = data(AdminHierarchyTable)
      .map(populateAttributes)

    data + (AdminHierarchyTable -> ds)
  }

  private def populateAttributes(poi: AdminPlace): AdminPlace = {
    val attributes = extractors.flatMap { extractor =>
      val attributes = extractor.decodeFromOsm(poi)
      if (attributes.nonEmpty) {
        val json = Json.toJson(attributes)(extractor.writes).toString()
        Some(extractor.tag -> json)
      } else None
    }

    poi.copy(tags = poi.tags ++ attributes)
  }
}