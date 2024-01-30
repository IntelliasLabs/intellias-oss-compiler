package com.intellias.osm.compiler.display

import com.intellias.osm.DisplayConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.model.display.DisplayTile
import org.apache.spark.sql.SparkSession

class DisplayTilesReader(config: DisplayConfig) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    data + (DisplayTileTable -> data.get(DisplayTileTable).getOrElse(spark.read.parquet(config.interimStoragePath).as[DisplayTile]))
  }
}

object DisplayTilesReader {
  def apply(config: DisplayConfig): DisplayTilesReader = new DisplayTilesReader(config)
}