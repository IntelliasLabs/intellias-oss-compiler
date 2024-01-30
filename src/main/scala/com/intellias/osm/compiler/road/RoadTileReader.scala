package com.intellias.osm.compiler.road

import com.intellias.osm.RoadConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.model.road.RoadTile
import org.apache.spark.sql.SparkSession

case class RoadTileReader(config: RoadConfig) extends Processor {

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    data + (NdsRoadTileTable -> data.get(NdsRoadTileTable).getOrElse(spark.read.parquet(config.outPath).as[RoadTile]))
  }
}
