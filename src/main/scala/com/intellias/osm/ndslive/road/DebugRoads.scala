package com.intellias.osm.ndslive.road

import com.intellias.osm.common.{Processor, SharedProcessorData, Workflow}
import com.intellias.osm.compiler.road.NdsRoadTileTable
import com.intellias.osm.model.road.RoadTile
import com.intellias.osm.ndslive.FailedResult
import org.apache.spark.sql.{Encoders, SparkSession}


object DebugRoads {
  def main(args: Array[String]): Unit = {

    implicit val spark: SparkSession = SparkSession
      .builder()
      .appName("Osm to NDS Live")
      .config("spark.master", "local[*]")
      .config("spark.sql.autoBroadcastJoinThreshold", 1000000)
      .getOrCreate()

    val testWorkflow = new Workflow {
      override def processors: Seq[Processor] = Seq (
        new Processor {
          override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
            import spark.implicits._

            val schema = Encoders.product[FailedResult[RoadTile]].schema

            val roadByTiles = spark.read
              .schema(schema)
              .parquet("/Volumes/Macintosh HD/Users/andriidziuba/intellias/osm-nds/osm-src-files/testOut/failureCopy/NdsLiveRoadCharacteristicsProcessor/")
              .as[FailedResult[RoadTile]]
              .map(fr => fr.inputData)

            roadByTiles.show()

            data.copy(tables = data.tables + (NdsRoadTileTable -> roadByTiles))
          }
        },
//        NdsLiveRoadCharacteristicsProcessor(appConf.ndsConfig)
      )
    }

    testWorkflow.run()

  }
}
