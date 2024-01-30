package com.intellias.osm.compiler.poi

import com.intellias.osm.PoiConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import org.apache.spark.sql.{SaveMode, SparkSession}

case class PoiTileWriter(config: PoiConfig) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {

    data(NdsPoiTable)
      .repartition(1)
      .write
      .mode(SaveMode.Overwrite)
      .parquet(config.outPath)

    data
  }
}
