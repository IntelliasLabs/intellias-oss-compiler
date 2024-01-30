package com.intellias.osm.compiler.display

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.display.DisplayTilesCollector._
import com.intellias.osm.model.display.{DisplayArea, DisplayLine, DisplayPoint, DisplayTile}
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, SparkSession}

class DisplayTilesCollector(config: CommonConfig) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val buildings = data(BuildingFootprintTable).groupBy(col("tileId")).agg(collectDisplayAreas as "buildings")
    val areas = data(DisplayAreaTable).groupBy(col("tileId")).agg(collectDisplayAreas as "areas")
    val lines = data(DisplayLineTable).groupBy(col("tileId")).agg(collectDisplayLines as "lines")
    val points = data(DisplayPointTable).groupBy(col("tileId")).agg(collectDisplayPoints as "points")

    val tileIds = data(BuildingFootprintTable).select($"tileId")
      .unionAll(data(DisplayAreaTable).select($"tileId"))
      .unionAll(data(DisplayLineTable).select($"tileId"))
      .unionAll(data(DisplayPointTable).select($"tileId"))
      .select(col("tileId"))
      .distinct()

    val displayTiles = tileIds.alias("tiles")
      .join(buildings.alias("b"), $"tiles.tileId" === $"b.tileId", "left")
      .join(areas.alias("a"), $"tiles.tileId" === $"a.tileId", "left")
      .join(lines.alias("l"), $"tiles.tileId" === $"l.tileId", "left")
      .join(points.alias("p"), $"tiles.tileId" === $"p.tileId", "left")
      .select(
        $"tiles.tileId" as "tileId",
        coalesce($"b.buildings", emptyAreasUdf()) as "buildingFootprints",
        coalesce($"a.areas", emptyAreasUdf()) as "areas",
        coalesce($"l.lines", emptyLinesUdf()) as "lines",
        coalesce($"p.points", emptyPointsUdf()) as "points"
      )
      .as[DisplayTile]
      .persist(config.sparkStorageLevel)

    data + (DisplayTileTable -> displayTiles)
  }
}

object DisplayTilesCollector {
  def apply(config: CommonConfig): DisplayTilesCollector = new DisplayTilesCollector(config)

  private def collectDisplayPoints: Column = collect_list(
    struct(
      col("tileId"),
      col("localId"),
      col("originalId"),
      col("geometry"),
      col("featureType"),
      col("tags"),
      col("adminPlace")
    )
  )

  private def collectDisplayLines: Column = collect_list(
    struct(
      col("tileId"),
      col("localId"),
      col("originalId"),
      col("geometry"),
      col("featureType"),
      col("tags"),
      col("leftAdminPlaces"),
      col("rightAdminPlaces")
    )
  )

  private def collectDisplayAreas: Column = collect_list(
    struct(
      col("tileId"),
      col("localId"),
      col("originalId"),
      col("geometry"),
      col("featureType"),
      col("tags"),
      col("adminPlaces")
    )
  )

  case class IdMap(tileId: Int, originalId: String, alignedLocalId: Int)
  val emptyAreasUdf: UserDefinedFunction = udf(() => Array.empty[DisplayArea])
  val emptyLinesUdf: UserDefinedFunction = udf(() => Array.empty[DisplayLine])
  val emptyPointsUdf: UserDefinedFunction = udf(() => Array.empty[DisplayPoint])
}