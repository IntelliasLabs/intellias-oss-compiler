package com.intellias.osm.ndslive.name.road

import com.intellias.osm.{CommonConfig, NdsLiveConfig}
import com.intellias.osm.common.SharedProcessorData
import com.intellias.osm.compiler.admin.AdminTileTable
import com.intellias.osm.compiler.road.NdsRoadTileTable
import com.intellias.osm.model.admin.AdminTile
import com.intellias.osm.model.road.RoadTile
import com.intellias.osm.ndslive.name.NdsNameEnvironment
import com.intellias.osm.ndslive.{FailedResult, NdsWriter, SuccessResult}
import org.apache.spark.sql.functions.struct
import org.apache.spark.sql.{Dataset, SparkSession}

case class NdsRoadNameWriter(ndsConf: NdsLiveConfig, env: NdsNameEnvironment, commonConf: CommonConfig) extends NdsWriter[RoadAdminTile] {
  private val converter = NdsRoadNameConverter(ndsConf, env)
  override val failureKeyCreator: RoadAdminTile => String = data => s"NdsRoadName-${data.roadTile.tileId}"

  override def convert(data: SharedProcessorData)(implicit spark: SparkSession): Dataset[Either[FailedResult[RoadAdminTile], SuccessResult]] = {
    import spark.implicits._

    val adminTile: Dataset[AdminTile] = data(AdminTileTable).as("admin")
    val roadTile: Dataset[RoadTile] = data(NdsRoadTileTable).as("feature")


    roadTile.join(adminTile, $"feature.tileId" === $"admin.tileId")
      .select(
        struct(
          $"feature.tileId",
          $"feature.topologies",
          $"feature.intersections"
        ) as "roadTile",
        struct(
          $"admin.tileId",
          $"admin.adminPlaces"
        ) as "adminTile"
      ).as[RoadAdminTile]
      .persist(commonConf.sparkStorageLevel)
      .map(converter.convert)
  }
}
