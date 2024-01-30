package com.intellias.osm.ndslive.road.characteristics

import com.intellias.mobility.geo.tools.nds.NdsTools.coordRoadShiftByte
import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.model.road.RoadTile
import com.intellias.osm.ndslive.road.characteristics.position._
import com.intellias.osm.ndslive.road.characteristics.range._
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import nds.characteristics.instantiations.{CharacsRoadPositionAttributeSetList, CharacsRoadPositionAttributeSetMap, CharacsRoadRangeAttributeSetList, CharacsRoadRangeAttributeSetMap}
import nds.characteristics.layer.RoadCharacteristicsLayer
import nds.characteristics.metadata.RoadCharacsLayerContent.Values.{ROAD_POSITION_SETS, ROAD_RANGE_SETS}
import zserio.runtime.io.SerializeUtil

import scala.util.{Failure, Success, Try}

case class NdsLiveRoadCharacteristicsConverter(conf: NdsLiveConfig) extends NdsConverter[RoadTile] with StrictLogging {
  private val roadCharsFilename: Int => String = tileId => s"${conf.roadCharacteristicsLayerPrefix}$tileId"

  private val rangeAttrBuilders: Seq[RangeAttributeValueBuilder] = Seq(
    RoadTypeBuilder,
    RoadFunctionalClassBuilder,
    RoadCarpoolBuilder,
    RoadPavementTypeBuilder,
    RoadHasSidewalkBuilder,
    RoadMovableBridgeBuilder,
    RoadNumLaneBuilder,
    RoadHasStreetLightsBuilder,
    RoadPhysicalWidthBuilder,
    RoadTrafficCalmingBuilder,
    RoadStartOrDestinationRoadOnlyBuilder,
    RoadSharedRoadSurfaceWithPedestriansBuilder,
    RoadHasPedestrianCrossingBuilder,
    RoadRailwayCrossingBuilder,
    RoadInBusinessDistrictBuilder
  )


  private val posAttrBuilders: Seq[PositionAttributeValueBuilder] = Seq(RoadStopLineBuilder, RoadPorterBuilder, RoadCheckpointBuilder, RoadTollStationBuilder)

  def convert(data: RoadTile): Either[FailedResult[RoadTile], SuccessResult] = Try {
    val rangeAttrSet = rangeAttrBuilders.flatMap{b =>
      b.buildAttributes(data.topologies)
    }
    val posAttrSet = posAttrBuilders.flatMap(b => b.buildAttributes(data.topologies))

    SuccessResult(Array((roadCharsFilename(data.tileId), SerializeUtil.serializeToBytes(buildLayer(rangeAttrSet, posAttrSet)))))
  } match {
    case Success(r) => Right(r)
    case Failure(exception) =>
      logger.error(s"Failed to export RoadCharacteristics for tile ${data.tileId}")
      Left(FailedResult(exception.getLocalizedMessage, data))
  }

  private def buildLayer(attrs: Iterable[CharacsRoadRangeAttributeSetMap], posAttrs: Iterable[CharacsRoadPositionAttributeSetMap]): RoadCharacteristicsLayer = {
    val attrSetList = new CharacsRoadRangeAttributeSetList(coordRoadShiftByte, attrs.size, attrs.toArray)
    val attrPosSetList = new CharacsRoadPositionAttributeSetList(coordRoadShiftByte, posAttrs.size, posAttrs.toArray)

    val roadCharacteristicsLayer = new RoadCharacteristicsLayer()
    roadCharacteristicsLayer.setShift(coordRoadShiftByte)
    roadCharacteristicsLayer.setContent(ROAD_RANGE_SETS.or(ROAD_POSITION_SETS))
    roadCharacteristicsLayer.setCharacsRoadRangeSets(attrSetList)
    roadCharacteristicsLayer.setCharacsRoadPositionSets(attrPosSetList)

    roadCharacteristicsLayer
  }
}






