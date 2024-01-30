package com.intellias.osm.ndslive.display

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.SharedProcessorData
import com.intellias.osm.compiler.display.{DisplayEnvironment, DisplayTileTable}
import com.intellias.osm.model.display.DisplayTile
import com.intellias.osm.ndslive.display.attributes.NdsDisplayAttributeConverter
import com.intellias.osm.ndslive.{FailedResult, NdsWriter, SuccessResult}
import org.apache.spark.sql.{Dataset, SparkSession}

case class NdsDisplayAttributeWriter(ndsConf: NdsLiveConfig, env: DisplayEnvironment) extends NdsWriter[DisplayTile] {
  private val converter = new NdsDisplayAttributeConverter(ndsConf)
  override val failureKeyCreator: DisplayTile => String = displayTile => s"NdsDisplayAttribute-${displayTile.tileId}"

  override def convert(data: SharedProcessorData)(implicit spark: SparkSession): Dataset[Either[FailedResult[DisplayTile], SuccessResult]] = {
    import spark.implicits._
    data(DisplayTileTable).map(converter.convert)
  }
}