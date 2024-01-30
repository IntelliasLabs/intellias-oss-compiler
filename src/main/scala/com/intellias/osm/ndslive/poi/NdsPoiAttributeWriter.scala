package com.intellias.osm.ndslive.poi

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{SharedProcessorData, StorageService}
import com.intellias.osm.compiler.poi.NdsPoiTable
import com.intellias.osm.model.poi.PoiTile
import com.intellias.osm.ndslive.poi.attributes.NdsPoiAttributeConverter
import com.intellias.osm.ndslive.{FailedResult, NdsWriter, SuccessResult}
import org.apache.spark.sql.{Dataset, SparkSession}

case class NdsPoiAttributeWriter(ndsConf: NdsLiveConfig, env: StorageService)  extends NdsWriter[PoiTile] {
  private val converter = NdsPoiAttributeConverter(ndsConf)
  override val failureKeyCreator: PoiTile => String = poiTile => s"NdsPoiAttribute-${poiTile.tileId}"

  override def convert(data: SharedProcessorData)(implicit spark: SparkSession): Dataset[Either[FailedResult[PoiTile], SuccessResult]] = {
    import spark.implicits._

    data(NdsPoiTable).map(converter.convert)
  }
}
