package com.intellias.osm.compiler.road

import com.intellias.osm.RoadConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import org.apache.spark.sql.{SaveMode, SparkSession}

case class RoadTileWriter(config: RoadConfig) extends Processor {

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {

    data(NdsRoadTileTable)
      .repartition(1)
      .write
      .mode(SaveMode.Overwrite)
      .parquet(config.outPath)

    data
  }
}
