package com.intellias.osm.ndslive.poi

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{SharedProcessorData, StorageService}
import com.intellias.osm.compiler.poi.NdsPoiTable
import com.intellias.osm.model.poi.PoiTile
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, NdsWriter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.{Dataset, SparkSession}

case class NdsPoiWriter(ndsConf: NdsLiveConfig, env: StorageService) extends NdsWriter[PoiTile] with StrictLogging  {
  private val converter: NdsConverter[PoiTile]  = NdsPoiConverter(ndsConf)
  override val failureKeyCreator: PoiTile => String = poiTile => s"NdsPoi-${poiTile.tileId}"

  override def convert(data: SharedProcessorData)(implicit spark: SparkSession): Dataset[Either[FailedResult[PoiTile], SuccessResult]] = {
    import spark.implicits._

    data(NdsPoiTable).map(converter.convert)
  }
}
