package com.intellias.osm.ndslive.display

import com.intellias.osm.{CommonConfig, NdsLiveConfig}
import com.intellias.osm.common.SharedProcessorData
import com.intellias.osm.compiler.display.{DisplayEnvironment, DisplayTileTable}
import com.intellias.osm.model.display.DisplayTile
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, NdsWriter, SuccessResult}
import org.apache.spark.sql.{Dataset, SparkSession}

case class NdsDisplayWriter(ndsConf: NdsLiveConfig,
                            commonConf: CommonConfig,
                            env: DisplayEnvironment) extends NdsWriter[DisplayTile] with Serializable {
  private val converter: NdsConverter[DisplayTile] = new NdsDisplayConverter(ndsConf, commonConf)
  override val failureKeyCreator: DisplayTile => String = displayTile => s"NdsDisplay-${displayTile.tileId}"

  override def convert(data: SharedProcessorData)(implicit spark: SparkSession): Dataset[Either[FailedResult[DisplayTile], SuccessResult]] = {
    import spark.implicits._

    data(DisplayTileTable).map(converter.convert)
  }
}