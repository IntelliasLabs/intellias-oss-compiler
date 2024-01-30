package com.intellias.osm.ndslive.name

import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.admin.{AdminHierarchyTable, AdminTileTable}
import com.intellias.osm.model.admin.AdminTile
import com.intellias.osm.ndslive.name.NdsAdminTileProcessor.{ndsAdminTileWindow, toTileSet}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.{UserDefinedFunction, Window, WindowSpec}
import org.apache.spark.sql.functions._

case class NdsAdminTileProcessor(env: NdsNameEnvironment) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val adminPlaces = data(AdminHierarchyTable)
      .withColumn("tileId", explode($"coveredTiles"))
      .withColumn("ndsId", row_number().over(ndsAdminTileWindow))
      .groupBy($"tileId")
      .agg(
        collect_list(
          struct(
            $"ndsId",
            struct(
              $"adminPlaceId",
              $"areaId",
              $"isoCountryCode",
              $"isoSubCountryCode",
              $"parents",
              $"adminLevel",
              $"adminType",
              $"tags",
              $"area",
              toTileSet($"tileId") as "coveredTiles"
            ) as "adminPlace",

          )
        ) as "adminPlaces")
      .select($"tileId", $"adminPlaces")
      .as[AdminTile]

    data + (AdminTileTable -> adminPlaces)
  }
}

object NdsAdminTileProcessor {
  val ndsAdminTileWindow: WindowSpec = Window.partitionBy(col("tileId")).orderBy("adminLevel", "adminPlaceId")

  def toTileSet: UserDefinedFunction = udf((tileId: Long) => Set(tileId))
}
