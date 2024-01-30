package com.intellias.osm.compiler.poi

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.poi.PoiPertTileProcessor.{aggregatePois, ndsPoiTileWindow}
import com.intellias.osm.model.poi.PoiTile
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.{Window, WindowSpec}
import org.apache.spark.sql.functions.{col, collect_list, row_number, struct}

case class PoiPertTileProcessor(commonConf: CommonConfig) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val poisPerTile = data(PoiTable)
      .select(
        $"poiId",
        $"longitude",
        $"latitude",
        $"tileId",
        $"tags",
        $"categories",
        $"children",
        $"parents",
        $"adminPlace"
      )
      .withColumn("ndsId", row_number().over(ndsPoiTileWindow))
      .groupBy(col("tileId"))
      .agg(aggregatePois as "pois")
      .as[PoiTile]
      .persist(commonConf.sparkStorageLevel)

    data + (NdsPoiTable -> poisPerTile)
  }
}

object PoiPertTileProcessor {

  val ndsPoiTileWindow: WindowSpec = Window.partitionBy(col("tileId")).orderBy("poiId")

  private def aggregatePois = collect_list(
    struct(
      col("poiId"),
      col("longitude"),
      col("latitude"),
      col("tileId"),
      col("tags"),
      col("ndsId"),
      col("categories"),
      col("children"),
      col("parents"),
      col("adminPlace")
    )
  )
}
