package com.intellias.osm.ndslive.road.rules

import com.intellias.mobility.geo.tools.nds.NdsTools.coordRoadShiftByte
import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.model.road.RoadTile
import com.intellias.osm.ndslive.road.rules.range.{RoadAdrTunnelBuilder, RoadAdvisorySpeedBuilder, RoadDrivingSideBuilder, RoadEnforcementZoneBuilder, RoadMaxSpeedBuilder, RoadMinSpeedBuilder, RoadOvertakingProhibitionBuilder, RoadProhibitedParkingBuilder, RoadProhibitedPassageBuilder, RoadProhibitedStoppingBuilder, RoadRoadworksBuilder, RoadSeasonalClosedBuilder, RoadTrafficZoneBuilder, RulesRangeAttributeValueBuilder}
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import nds.rules.instantiations.{RulesRoadRangeAttributeSetList, RulesRoadRangeAttributeSetMap}
import nds.rules.layer.RoadRulesLayer
import nds.rules.metadata.RoadRulesLayerContent.Values.ROAD_RANGE_SETS
import zserio.runtime.io.SerializeUtil

import scala.util.{Failure, Success, Try}

case class NdsLiveRoadRulesConverter(conf: NdsLiveConfig) extends NdsConverter[RoadTile] with StrictLogging {
  private val roadRulesFilename: Int => String = tileId => s"${conf.roadRulesLayerPrefix}$tileId"

  private val rangeAttrBuilders: Seq[RulesRangeAttributeValueBuilder] = Seq(RoadMaxSpeedBuilder, RoadMinSpeedBuilder,
    RoadAdvisorySpeedBuilder, RoadOvertakingProhibitionBuilder, RoadProhibitedPassageBuilder, RoadEnforcementZoneBuilder,
    RoadDrivingSideBuilder, RoadTrafficZoneBuilder, RoadAdrTunnelBuilder, RoadSeasonalClosedBuilder,
    RoadProhibitedParkingBuilder, RoadProhibitedStoppingBuilder, RoadRoadworksBuilder)

  def convert(data: RoadTile): Either[FailedResult[RoadTile], SuccessResult] = Try {
    val rangeAttrSet = rangeAttrBuilders.flatMap(_.buildAttributes(data.topologies))

    SuccessResult(Array((roadRulesFilename(data.tileId), SerializeUtil.serializeToBytes(buildLayer(rangeAttrSet)))))
  } match {
    case Success(r) => Right(r)
    case Failure(exception) =>
      logger.error(s"Failed to export RoadRules for tile ${data.tileId}", exception)
      Left(FailedResult(exception.getLocalizedMessage, data))
  }

  private def buildLayer(attrs: Iterable[RulesRoadRangeAttributeSetMap]): RoadRulesLayer = {
    val attrSetList = new RulesRoadRangeAttributeSetList(coordRoadShiftByte, attrs.size, attrs.toArray)

    val layer = new RoadRulesLayer()
    layer.setShift(coordRoadShiftByte)
    layer.setContent(ROAD_RANGE_SETS)
    layer.setRoadRangeAttributeSets(attrSetList)

    layer
  }
}
