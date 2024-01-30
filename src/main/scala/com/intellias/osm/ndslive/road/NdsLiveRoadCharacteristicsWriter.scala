package com.intellias.osm.ndslive.road

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{SharedProcessorData, StorageService}
import com.intellias.osm.compiler.road.NdsRoadTileTable
import com.intellias.osm.model.road.RoadTile
import com.intellias.osm.ndslive.road.characteristics.NdsLiveRoadCharacteristicsConverter
import com.intellias.osm.ndslive.{FailedResult, NdsWriter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.{Dataset, SparkSession}

case class NdsLiveRoadCharacteristicsWriter(ndsConf: NdsLiveConfig, env: StorageService) extends NdsWriter[RoadTile] with StrictLogging {
  private val converter = NdsLiveRoadCharacteristicsConverter(ndsConf)
  override val failureKeyCreator: RoadTile => String = data => s"NdsRoadCharacteristics-${data.tileId}"

  def convert(data: SharedProcessorData)(implicit spark: SparkSession): Dataset[Either[FailedResult[RoadTile], SuccessResult]] = {
    import spark.implicits._
    data(NdsRoadTileTable).map(converter.convert)
  }
}