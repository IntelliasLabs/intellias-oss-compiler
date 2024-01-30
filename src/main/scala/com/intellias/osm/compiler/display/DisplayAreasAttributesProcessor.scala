package com.intellias.osm.compiler.display

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.attributes.AttributeExtractorLike
import com.intellias.osm.compiler.display.attribute.extractor.area.{DisplayAreaNameExtractor, GlobalIdExtractor}
import com.intellias.osm.model.display.DisplayArea
import org.apache.spark.sql.SparkSession
import play.api.libs.json.Json

class DisplayAreasAttributesProcessor(config: CommonConfig, env: DisplayEnvironment) extends Processor {
  // TODO: add DRAWING_ORDER here and for other display features
  val extractors: Seq[AttributeExtractorLike[DisplayArea]] = Seq(GlobalIdExtractor, new DisplayAreaNameExtractor(env))

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val attributedAreas = data(DisplayAreaTable)
      .map(populateAreaAttributes)
      .persist(config.sparkStorageLevel)

    data + (DisplayAreaTable -> attributedAreas)
  }

  private def populateAreaAttributes(displayArea: DisplayArea): DisplayArea = {
    val areaAttrs = extractors
      .flatMap { extractor =>
        val attrs = extractor.decodeFromOsm(displayArea)
        if (attrs.nonEmpty) {
          Some(extractor.tag -> Json.toJson(attrs)(extractor.writes).toString)
        } else {
          None
        }
      }

    displayArea.copy(tags = displayArea.tags ++ areaAttrs)
  }
}

object DisplayAreasAttributesProcessor {
  def apply(config: CommonConfig, env: DisplayEnvironment) = new DisplayAreasAttributesProcessor(config, env)
}
