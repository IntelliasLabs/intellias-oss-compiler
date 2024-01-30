package com.intellias.osm.compiler.display

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.attributes.AttributeExtractorLike
import com.intellias.osm.compiler.display.attribute.extractor.line.DisplayLineNameExtractor
import com.intellias.osm.model.display.DisplayLine
import org.apache.spark.sql.SparkSession
import play.api.libs.json.Json

class DisplayLinesAttributesProcessor(config: CommonConfig, env: DisplayEnvironment) extends Processor {
  val extractors: Seq[AttributeExtractorLike[DisplayLine]] = Seq(new DisplayLineNameExtractor(env))

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    data + (DisplayLineTable -> data(DisplayLineTable).map(populateLineAttributes))
  }

  private def populateLineAttributes(displayLine: DisplayLine): DisplayLine = {
    val lineAttrs = extractors
      .flatMap { extractor =>
        val attrs = extractor.decodeFromOsm(displayLine)
        if (attrs.nonEmpty) {
          Some(extractor.tag -> Json.toJson(attrs)(extractor.writes).toString())
        } else {
          None
        }
      }
    displayLine.copy(tags = displayLine.tags ++ lineAttrs)
  }
}

object DisplayLinesAttributesProcessor {
  def apply(config: CommonConfig, env: DisplayEnvironment) = new DisplayLinesAttributesProcessor(config, env)
}