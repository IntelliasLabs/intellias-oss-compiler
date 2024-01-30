package com.intellias.osm.ndslive.poi.attributes

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.model.poi.PoiTile
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import nds.core.attributemap.ConditionTypeCodeCollection
import nds.core.conditions.ConditionTypeCode
import nds.poi.instantiations.{PoiAttributeMap, PoiAttributeMapList, PoiAttributeMapListHeader}
import nds.poi.layer.PoiAttributeLayer
import nds.poi.metadata.PoiAttributeLayerContent
import zserio.runtime.io.SerializeUtil

import scala.util.{Failure, Success, Try}

case class NdsPoiAttributeConverter(conf: NdsLiveConfig) extends NdsConverter[PoiTile] with StrictLogging {
  private val poiAttributeFileKey: Int => String = tileId => s"${conf.poiAttrPrefix}$tileId"
  private val attrBuilders: Seq[NdsPoiAttributeBuilder] = Seq(NdsPoiOpen24SevenBuilder, NdsPoiBrandBuilder, NdsPoiPaymentMethodsBuilder,
    NdsPoiEnergyTypeBuilder, NdsPoiEvChargingDetailsBuilder)


  override def convert(data: PoiTile): Either[FailedResult[PoiTile], SuccessResult] = Try {
    val attrMaps: Seq[PoiAttributeMap] = attrBuilders.flatMap(builder => builder.buildAttributes(data.pois))

    val header = new PoiAttributeMapListHeader(attrMaps.size.toShort)
    header.setAttributeTypeCode(attrMaps.map(_.getAttributeTypeCode).toArray)
    header.setConditionType(attrMaps.map(toConditionTypeCollection).toArray)

    val mapList: PoiAttributeMapList = new PoiAttributeMapList(0.toByte)
    mapList.setNumMaps(attrMaps.size.toShort)
    mapList.setMaps(attrMaps.toArray)
    mapList.setHeader(header)

    val poiAttrLayer = new PoiAttributeLayer()
    poiAttrLayer.setContent(PoiAttributeLayerContent.Values.POI_ATTRIBUTE_MAPS)
    poiAttrLayer.setPoiAttributeMaps(mapList)

    SuccessResult(Array((poiAttributeFileKey(data.tileId), SerializeUtil.serializeToBytes(poiAttrLayer))))
  } match {
    case Success(r) => Right(r)
    case Failure(exception) =>
      logger.error(s"Can't build POIs for tileId: ${data.tileId}", exception)
      Left(FailedResult(exception.getMessage, data))
  }

  private def toConditionTypeCollection(attrCond: PoiAttributeMap): ConditionTypeCodeCollection = {
    val conditionTypeCodes = attrCond.getAttributeConditions
      .foldLeft(Set.empty[ConditionTypeCode]) { (set, conditionList) =>
        set ++ conditionList.getConditionList.map(_.getConditionTypeCode)
      }
    new ConditionTypeCodeCollection(conditionTypeCodes.toArray)
  }
}
