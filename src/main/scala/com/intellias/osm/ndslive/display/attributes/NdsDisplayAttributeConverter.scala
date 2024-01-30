package com.intellias.osm.ndslive.display.attributes

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.model.display.{DisplayArea, DisplayPoint, DisplayTile}
import com.intellias.osm.ndslive.display.attributes.NdsDisplayAttributeConverter.{areaBuilders, pointBuilders}
import com.intellias.osm.ndslive.display.attributes.area.building._
import com.intellias.osm.ndslive.display.attributes.area.{NdsAreaGlobalIdBuilder, NdsDisplayAreaAttributeBuilder}
import com.intellias.osm.ndslive.display.attributes.point.{NdsCityPopulationBuilder, NdsDisplayPointAttributeBuilder}
import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import nds.display.details.instantiations.{DisplayAreaAttributeSetList, DisplayAreaAttributeSetMap, DisplayPointAttributeSetList, DisplayPointAttributeSetMap}
import nds.display.details.layer.DisplayAttributeLayer
import nds.display.details.metadata.DisplayAttributeLayerContent
import zserio.runtime.io.SerializeUtil

import scala.util.{Failure, Success, Try}

class NdsDisplayAttributeConverter(ndsConfig: NdsLiveConfig) extends NdsConverter[DisplayTile] with StrictLogging with Serializable {
  private val displayAttributeFileKey: Int => String = tileId => s"${ndsConfig.displayAttrPrefix}$tileId"

  override def convert(displayTile: DisplayTile): Either[FailedResult[DisplayTile], SuccessResult] = Try {
    val displayAttributeLayer = new DisplayAttributeLayer()
    displayAttributeLayer.setContent(
      DisplayAttributeLayerContent.Values.DISPLAY_AREA_SETS.or(DisplayAttributeLayerContent.Values.DISPLAY_POINT_SETS))
    displayAttributeLayer.setDisplayAreaAttributeSets(buildAreaAttributeSets(displayTile.buildingFootprints ++ displayTile.areas))
    displayAttributeLayer.setDisplayPointAttributeSets(buildPointAttributeSets(displayTile.points))

    Array(
      (displayAttributeFileKey(displayTile.tileId), SerializeUtil.serializeToBytes(displayAttributeLayer))
    )
  } match {
    case Success(r) => Right(SuccessResult(r))
    case Failure(e) =>
      logger.error(s"Failed to compile display attributes for tile ${displayTile.tileId}: $e")
      Left(FailedResult(e.getMessage, displayTile))
  }

  private def buildAreaAttributeSets(areas: Seq[DisplayArea]): DisplayAreaAttributeSetList = {
    val attrSetMaps: Array[DisplayAreaAttributeSetMap] = areaBuilders.flatMap(_.buildAttributes(areas))
    val attrSetList = new DisplayAreaAttributeSetList(displayCoordinateShift)
    attrSetList.setNumAttributeSets(attrSetMaps.length)
    attrSetList.setSets(attrSetMaps)
    attrSetList
  }

  private def buildPointAttributeSets(points: Seq[DisplayPoint]): DisplayPointAttributeSetList = {
    val attrSetMaps: Array[DisplayPointAttributeSetMap] = pointBuilders.flatMap(_.buildAttributes(points))
    val attrSetList = new DisplayPointAttributeSetList(displayCoordinateShift)
    attrSetList.setNumAttributeSets(attrSetMaps.length)
    attrSetList.setSets(attrSetMaps)
    attrSetList
  }
}

object NdsDisplayAttributeConverter {
  val areaBuilders: Array[NdsDisplayAreaAttributeBuilder[_]] = Array(
    NdsAreaGlobalIdBuilder,
    NdsBuildingFloorsBuilder,
    NdsBuildingHeightBuilder,
    NdsBuildingWallsColorBuilder,
    NdsBuildingRoofColorBuilder
  )

  val pointBuilders: Array[NdsDisplayPointAttributeBuilder[_]] = Array(NdsCityPopulationBuilder)
}