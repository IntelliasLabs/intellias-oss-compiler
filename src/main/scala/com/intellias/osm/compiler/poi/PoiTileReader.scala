package com.intellias.osm.compiler.poi

import com.intellias.osm.PoiConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.model.poi.PoiTile
import org.apache.spark.sql.SparkSession

case class PoiTileReader(config: PoiConfig) extends Processor {

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    data + (NdsPoiTable -> data.get(NdsPoiTable).getOrElse(spark.read.parquet(config.outPath).as[PoiTile]))
  }
}
