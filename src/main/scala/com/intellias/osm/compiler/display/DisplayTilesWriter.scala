package com.intellias.osm.compiler.display

import com.intellias.osm.DisplayConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import org.apache.spark.sql.{SaveMode, SparkSession}

class DisplayTilesWriter(config: DisplayConfig) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    data(DisplayTileTable)
      .repartition(1)
      .write
      .mode(SaveMode.Overwrite)
      .parquet(config.interimStoragePath)

    data
  }
}

object DisplayTilesWriter {
  def apply(config: DisplayConfig): DisplayTilesWriter = new DisplayTilesWriter(config)
}
