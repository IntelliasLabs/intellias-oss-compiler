package com.intellias.osm.compiler.display

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.display.DisplayTilesCollector.IdMap
import com.intellias.osm.model.display.DisplayArea
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, row_number}
import org.apache.spark.sql.{Dataset, SparkSession}

class DisplayAreasLocalIdGenerator(config: CommonConfig) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    // Local IDs must be unique across display area features
    val localIdMapping = data(BuildingFootprintTable).select($"tileId", $"originalId", $"featureType")
      .union(data(DisplayAreaTable).select($"tileId", $"originalId", $"featureType"))
      .select(
        col("tileId"),
        col("originalId"),
        row_number().over(Window.partitionBy($"tileId").orderBy($"featureType", $"originalId")).as("alignedLocalId")
      )
      .as[IdMap]
      .persist(config.sparkStorageLevel)

    data ++ Seq(
      BuildingFootprintTable -> mapToLocalIds(data(BuildingFootprintTable), localIdMapping).persist(config.sparkStorageLevel),
      DisplayAreaTable -> mapToLocalIds(data(DisplayAreaTable), localIdMapping).persist(config.sparkStorageLevel)
    )
  }

  private def mapToLocalIds(areaFeatures: Dataset[DisplayArea], localIdMap: Dataset[IdMap])
                           (implicit spark: SparkSession): Dataset[DisplayArea] = {
    import spark.implicits._

    areaFeatures.alias("af")
      .join(localIdMap.alias("id_map"),
        $"af.tileId" === $"id_map.tileId" && $"af.originalId" === $"id_map.originalId",
        "inner"
      )
      .select(
        col("af.tileId") as "tileId",
        col("id_map.alignedLocalId").as("localId"),
        col("af.originalId"),
        col("geometry"),
        col("featureType"),
        col("tags"),
        col("adminPlaces")
      ).as[DisplayArea]
  }
}

object DisplayAreasLocalIdGenerator {
  def apply(config: CommonConfig) = new DisplayAreasLocalIdGenerator(config)
}