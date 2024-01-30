package com.intellias.osm.ndslive.name.display

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.SharedProcessorData
import com.intellias.osm.compiler.admin.AdminTileTable
import com.intellias.osm.compiler.display.DisplayTileTable
import com.intellias.osm.model.admin.AdminTile
import com.intellias.osm.model.display.DisplayTile
import com.intellias.osm.ndslive.name.NdsNameEnvironment
import com.intellias.osm.ndslive.{FailedResult, NdsWriter, SuccessResult}
import org.apache.spark.sql.functions.{col, struct}
import org.apache.spark.sql.{Dataset, SparkSession}

class NdsDisplayNameWriter(val ndsConf: NdsLiveConfig, val env: NdsNameEnvironment) extends NdsWriter[DisplayAdminTile] {
  private val converter = NdsDisplayNameConverter(ndsConf, env)
  override val failureKeyCreator: DisplayAdminTile => String = data => s"NdsDisplayName-${data.displayTile.tileId}"

  override def convert(data: SharedProcessorData)
                      (implicit spark: SparkSession): Dataset[Either[FailedResult[DisplayAdminTile], SuccessResult]] = {
    import spark.implicits._

    val adminTiles: Dataset[AdminTile] = data(AdminTileTable).as("admin")
    val displayTiles: Dataset[DisplayTile] = data(DisplayTileTable).as("display")

    displayTiles.join(adminTiles, $"display.tileId" === $"admin.tileId")
      .select(
        struct(
          col("display.tileId"),
          col("buildingFootprints"),
          col("areas"),
          col("lines"),
          col("points")
        ) as "displayTile",
        struct(
          col("admin.tileId"),
          col("adminPlaces")
        ) as "adminTile"
      ).as[DisplayAdminTile]
      .map(converter.convert)
  }
}

object NdsDisplayNameWriter {
  def apply(ndsConf: NdsLiveConfig, env: NdsNameEnvironment) = new NdsDisplayNameWriter(ndsConf, env)
}