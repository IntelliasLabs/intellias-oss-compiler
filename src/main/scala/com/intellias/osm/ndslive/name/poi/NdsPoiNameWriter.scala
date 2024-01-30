package com.intellias.osm.ndslive.name.poi

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.SharedProcessorData
import com.intellias.osm.compiler.admin.AdminTileTable
import com.intellias.osm.compiler.poi.NdsPoiTable
import com.intellias.osm.model.admin.AdminTile
import com.intellias.osm.model.poi.PoiTile
import com.intellias.osm.ndslive.name.NdsNameEnvironment
import com.intellias.osm.ndslive.{FailedResult, NdsWriter, SuccessResult}
import org.apache.spark.sql.functions.struct
import org.apache.spark.sql.{Dataset, SparkSession}

case class NdsPoiNameWriter(ndsConf: NdsLiveConfig, env: NdsNameEnvironment)  extends NdsWriter[PoiAdminTile] {
  private val converter = NdsPoiNameConverter(ndsConf, env)
  override val failureKeyCreator: PoiAdminTile => String = data => s"NdsPoiName-${data.poiTile.tileId}"

  override def convert(data: SharedProcessorData)(implicit spark: SparkSession): Dataset[Either[FailedResult[PoiAdminTile], SuccessResult]] = {
    import spark.implicits._

    val adminTile: Dataset[AdminTile] = data(AdminTileTable).as("admin")
    val poiTile: Dataset[PoiTile] = data(NdsPoiTable).as("poi")

    poiTile.join(adminTile, $"poi.tileId" === $"admin.tileId")
      .select(
        struct(
          $"poi.tileId",
          $"poi.pois"
        ) as "poiTile",
        struct(
          $"admin.tileId",
          $"admin.adminPlaces"
        ) as "adminTile"
      ).as[PoiAdminTile]
      .map(converter.convert)
  }
}
