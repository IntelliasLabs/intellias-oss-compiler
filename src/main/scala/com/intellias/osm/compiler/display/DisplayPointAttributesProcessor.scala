package com.intellias.osm.compiler.display

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.attributes.AttributeExtractorLike
import com.intellias.osm.compiler.display.attribute.extractor.point.{DisplayPointNameExtractor, PopulationExtractor}
import com.intellias.osm.model.display.DisplayPoint
import org.apache.spark.sql.SparkSession
import play.api.libs.json.Json

class DisplayPointAttributesProcessor(config: CommonConfig, env: DisplayEnvironment) extends Processor {
  val extractors: Seq[AttributeExtractorLike[DisplayPoint]] = Seq(PopulationExtractor, new DisplayPointNameExtractor(env))

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val attributedPoints = data(DisplayPointTable).map(populatePointAttributes)
    data + (DisplayPointTable -> attributedPoints)
  }

  private def populatePointAttributes(displayPoint: DisplayPoint): DisplayPoint = {
    val pointAttributes = extractors
      .flatMap { extractor =>
        val attrs = extractor.decodeFromOsm(displayPoint)
        if (attrs.nonEmpty) {
          Some(extractor.tag -> Json.toJson(attrs)(extractor.writes).toString)
        } else {
          None
        }
      }

    displayPoint.copy(tags = displayPoint.tags ++ pointAttributes)
  }
}

object DisplayPointAttributesProcessor {
  def apply(config: CommonConfig, env: DisplayEnvironment) = new DisplayPointAttributesProcessor(config, env)
}
