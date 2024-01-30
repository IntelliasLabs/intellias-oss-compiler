package com.intellias.osm.compiler.display

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.attributes.AttributeExtractorLike
import com.intellias.osm.compiler.display.attribute.extractor.area.building.{FloorCountExtractor, HeightExtractor, RoofColorExtractor, WallsColorExtractor}
import com.intellias.osm.compiler.display.attribute.extractor.area.{DisplayAreaNameExtractor, GlobalIdExtractor}
import com.intellias.osm.model.display.DisplayArea
import org.apache.spark.sql.SparkSession
import play.api.libs.json.Json

class BuildingAttributesProcessor(config: CommonConfig, env: DisplayEnvironment) extends Processor {
  val extractors: Seq[AttributeExtractorLike[DisplayArea]] = Seq(
    FloorCountExtractor, HeightExtractor, GlobalIdExtractor, WallsColorExtractor, RoofColorExtractor,
    new DisplayAreaNameExtractor(env)
  )

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val attributedBuildings = data(BuildingFootprintTable)
      .map(populateBuildingAttributes)
      .persist(config.sparkStorageLevel)

    data + (BuildingFootprintTable -> attributedBuildings)
  }

  private def populateBuildingAttributes(buildingFootprint: DisplayArea): DisplayArea = {
    val buildingAttrs = extractors
      .flatMap { extractor =>
        val attrs = extractor.decodeFromOsm(buildingFootprint)
        if (attrs.nonEmpty) {
          Some(extractor.tag -> Json.toJson(attrs)(extractor.writes).toString)
        } else {
          None
        }
      }

    buildingFootprint.copy(tags = buildingFootprint.tags ++ buildingAttrs)
  }
}

object BuildingAttributesProcessor {
  def apply(config: CommonConfig, env: DisplayEnvironment) = new BuildingAttributesProcessor(config, env)
}