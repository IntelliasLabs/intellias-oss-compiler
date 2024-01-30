package com.intellias.osm.ndslive.name.poi

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.ndslive.name.{NdsAdminTileBuilder, NdsNameEnvironment}
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import nds.core.attributemap.ConditionTypeCodeCollection
import nds.core.conditions.ConditionTypeCode
import nds.name.instantiations.{NamePoiAttributeMap, NamePoiAttributeMapList, NamePoiAttributeMapListHeader}
import nds.name.layer.PoiNameLayer
import nds.name.metadata.PoiNameLayerContent
import zserio.runtime.io.SerializeUtil

import scala.util.{Failure, Success, Try}

case class NdsPoiNameConverter(conf: NdsLiveConfig, env: NdsNameEnvironment) extends NdsConverter[PoiAdminTile] with StrictLogging {
  private val poiAttributeFileKey: Int => String   = tileId => s"${conf.poiNamePrefix}$tileId"
  private val attrBuilders: Seq[NdsPoiNameBuilder] = Seq(NdsPoiNameBuilderImpl(env), NdsNameAdminBuilder)

  override def convert(data: PoiAdminTile): Either[FailedResult[PoiAdminTile], SuccessResult] =
    Try {
      val adminTileBuilder: NdsAdminTileBuilder = NdsAdminTileBuilder(data.adminTile)

      val attrMaps: Seq[NamePoiAttributeMap] =
        attrBuilders.flatMap(builder => builder.buildAttributes(data.poiTile.pois, adminTileBuilder))

      val header = new NamePoiAttributeMapListHeader(attrMaps.size.toShort)
      header.setAttributeTypeCode(attrMaps.map(_.getAttributeTypeCode).toArray)
      header.setConditionType(attrMaps.map(toConditionTypeCollection).toArray)

      val mapList: NamePoiAttributeMapList = new NamePoiAttributeMapList(0.toByte)
      mapList.setNumMaps(attrMaps.size.toShort)
      mapList.setMaps(attrMaps.toArray)
      mapList.setHeader(header)

      val poiAttrLayer = new PoiNameLayer()
      poiAttrLayer.setContent(PoiNameLayerContent.Values.POI_MAPS.or(PoiNameLayerContent.Values.ADMIN_HIERARCHY))
      poiAttrLayer.setPoiAttributeMaps(mapList)
      poiAttrLayer.setAdminHierarchyElementDefinitions(adminTileBuilder.getAdminHierarchy)

      SuccessResult(Array((poiAttributeFileKey(data.poiTile.tileId), SerializeUtil.serializeToBytes(poiAttrLayer))))
    } match {
      case Success(r) => Right(r)
      case Failure(exception) =>
        logger.error(s"Can't build POIs for tileId: ${data.poiTile.tileId}", exception)
        Left(FailedResult(exception.getMessage, data))
    }

  private def toConditionTypeCollection(attrCond: NamePoiAttributeMap): ConditionTypeCodeCollection = {
    val conditionTypeCodes = attrCond.getAttributeConditions
      .foldLeft(Set.empty[ConditionTypeCode]) { (set, conditionList) =>
        set ++ conditionList.getConditionList.map(_.getConditionTypeCode)
      }
    new ConditionTypeCodeCollection(conditionTypeCodes.toArray)
  }
}
