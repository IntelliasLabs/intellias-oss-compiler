package com.intellias.osm.ndslive.poi

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.model.poi.{POI, PoiTile}
import com.intellias.osm.ndslive.tools.NdsLiveTools
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import nds.poi.layer.PoiLayer
import nds.poi.poi.Poi
import zserio.runtime.io.SerializeUtil

import scala.util.{Failure, Success, Try}

case class NdsPoiConverter(conf: NdsLiveConfig) extends NdsConverter[PoiTile] with StrictLogging {
  val poiFileKey: Int => String = tileId => s"${conf.poiPrefix}$tileId"

  override def convert(data: PoiTile): Either[FailedResult[PoiTile], SuccessResult] = Try {
    val pois = data.pois.map(buildNdsPoi)

    SuccessResult(Array(
      (poiFileKey(data.tileId), SerializeUtil.serializeToBytes(new PoiLayer(pois))),
    ))
  } match {
    case Success(r) => Right(r)
    case Failure(exception) =>
      logger.error(s"Can't build POIs for tileId: ${data.tileId}", exception)
      Left(FailedResult(exception.getMessage, data))
  }

  private def buildNdsPoi(ndsPoi: POI): Poi = {
    val poi = new Poi()
    poi.setPoiId(ndsPoi.ndsId)
    poi.setPosition(NdsLiveTools.position2DNoShift(ndsPoi.longitude, ndsPoi.latitude))
    poi.setCategoryIdList(ndsPoi.categories)

    poi
  }
}
