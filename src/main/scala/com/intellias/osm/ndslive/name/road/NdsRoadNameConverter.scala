package com.intellias.osm.ndslive.name.road

import com.intellias.mobility.geo.tools.nds.NdsTools
import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.ndslive.name.{NdsAdminTileBuilder, NdsNameEnvironment}
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import nds.core.attributemap.ConditionTypeCodeCollection
import nds.core.conditions.ConditionTypeCode
import nds.name.instantiations.{NameRoadRangeAttributeMap, NameRoadRangeAttributeMapList, NameRoadRangeAttributeMapListHeader}
import nds.name.layer.RoadNameLayer
import nds.name.metadata.RoadNameLayerContent
import zserio.runtime.io.SerializeUtil

import scala.util.{Failure, Success, Try}

case class NdsRoadNameConverter(conf: NdsLiveConfig, env: NdsNameEnvironment) extends NdsConverter[RoadAdminTile] with StrictLogging {
  private val roadAttributeFileKey: Int => String   = tileId => s"${conf.roadNamePrefix}$tileId"
  private val attrBuilders: Seq[NdsRoadNameBuilder] = Seq(NdsRoadNameBuilderImpl(env), NdsNameAdminBuilder, NdsRoadNumberBuilder(env))

  override def convert(data: RoadAdminTile): Either[FailedResult[RoadAdminTile], SuccessResult] =
    Try {
      val adminTileBuilder: NdsAdminTileBuilder = NdsAdminTileBuilder(data.adminTile)

      val attrMaps: Seq[NameRoadRangeAttributeMap] =
        attrBuilders.flatMap(builder => builder.buildAttributes(data.roadTile.topologies, adminTileBuilder))

      val header = new NameRoadRangeAttributeMapListHeader(attrMaps.size.toShort)
      header.setAttributeTypeCode(attrMaps.map(_.getAttributeTypeCode).toArray)
      header.setConditionType(attrMaps.map(toConditionTypeCollection).toArray)

      val mapList: NameRoadRangeAttributeMapList = new NameRoadRangeAttributeMapList(NdsTools.coordNoShiftByte)
      mapList.setNumMaps(attrMaps.size.toShort)
      mapList.setMaps(attrMaps.toArray)
      mapList.setHeader(header)

      val roadAttrLayer = new RoadNameLayer()
      roadAttrLayer.setContent(RoadNameLayerContent.Values.ROAD_RANGE_MAPS.or(RoadNameLayerContent.Values.ADMIN_HIERARCHY))
      roadAttrLayer.setRoadRangeAttributeMaps(mapList)
      roadAttrLayer.setAdminHierarchyElementDefinitions(adminTileBuilder.getAdminHierarchy)

      SuccessResult(Array((roadAttributeFileKey(data.roadTile.tileId), SerializeUtil.serializeToBytes(roadAttrLayer))))
    } match {
      case Success(r) => Right(r)
      case Failure(exception) =>
        logger.error(s"Can't build Road's for tileId: ${data.roadTile.tileId}", exception)
        Left(FailedResult(exception.getMessage, data))
    }

  private def toConditionTypeCollection(attrCond: NameRoadRangeAttributeMap): ConditionTypeCodeCollection = {
    val conditionTypeCodes = attrCond.getAttributeConditions
      .foldLeft(Set.empty[ConditionTypeCode]) { (set, conditionList) =>
        set ++ conditionList.getConditionList.map(_.getConditionTypeCode)
      }
    new ConditionTypeCodeCollection(conditionTypeCodes.toArray)
  }
}
