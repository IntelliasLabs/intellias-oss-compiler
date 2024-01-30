package com.intellias.osm.ndslive.name.display

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.model.display.{DisplayArea, DisplayLine, DisplayPoint}
import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.intellias.osm.ndslive.name.display.NdsDisplayNameConverter.{areaNameBuilders, lineNameBuilders, pointNameBuilders}
import com.intellias.osm.ndslive.name.display.area.{NdsAdminAreaNameBuilder, NdsAreaLabelNameBuilder, NdsAreaNameAdminBuilder, NdsBuildingNameBuilder, NdsDisplayAreaNameBuilder, NdsNatureAreaNameBuilder, NdsWaterAreaNameBuilder}
import com.intellias.osm.ndslive.name.display.line.{NdsDisplayLineNameBuilder, NdsLineLabelNameBuilder, NdsLineNameAdminBuilder, NdsWaterLineNameBuilder}
import com.intellias.osm.ndslive.name.display.point.{NdsAdminCenterNameBuilder, NdsDisplayPointNameBuilder, NdsMountainPeakNameBuilder}
import com.intellias.osm.ndslive.name.{NdsAdminTileBuilder, NdsNameEnvironment}
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import nds.core.attributemap.ConditionTypeCodeCollection
import nds.name.instantiations.{NameDisplayAreaAttributeMap, NameDisplayAreaAttributeMapList, NameDisplayAreaAttributeMapListHeader, NameDisplayLineRangeAttributeMap, NameDisplayLineRangeAttributeMapList, NameDisplayLineRangeAttributeMapListHeader, NameDisplayPointAttributeMap, NameDisplayPointAttributeMapList, NameDisplayPointAttributeMapListHeader}
import nds.name.layer.DisplayNameLayer
import nds.name.metadata.DisplayNameLayerContent
import nds.name.metadata.DisplayNameLayerContent.Values._
import zserio.runtime.io.SerializeUtil

import scala.util.{Failure, Success, Try}

class NdsDisplayNameConverter(conf: NdsLiveConfig, env: NdsNameEnvironment) extends NdsConverter[DisplayAdminTile] with StrictLogging with Serializable {
  private def displayNameFileKey(tileId: Int): String = s"${conf.displayNamePrefix}$tileId"

  override def convert(data: DisplayAdminTile): Either[FailedResult[DisplayAdminTile], SuccessResult] = Try {
    val adminTileBuilder: NdsAdminTileBuilder = NdsAdminTileBuilder(data.adminTile)

    val maybeAreaNames = buildDisplayAreaNames(data.displayTile.areas ++ data.displayTile.buildingFootprints, adminTileBuilder)
    val maybeLineNames = buildDisplayLineNames(data.displayTile.lines, adminTileBuilder)
    val maybePointNames = buildDisplayPointNames(data.displayTile.points)
    val maybeAdminHierarchies = {
      val adminHierarchyElements = adminTileBuilder.getAdminHierarchy
      if (adminHierarchyElements.isEmpty) None else Some(adminHierarchyElements)
    }

    // if there are no names for any of the display objects then it makes no sense to populate admin hierarchy as well
    if (Seq(maybeAreaNames, maybeLineNames, maybePointNames).exists(_.isDefined)) {
      val layerContentType: DisplayNameLayerContent = Map(
        DISPLAY_AREA_MAPS -> maybeAreaNames,
        DISPLAY_LINE_RANGE_MAPS -> maybeLineNames,
        DISPLAY_POINT_MAPS -> maybePointNames,
        ADMIN_HIERARCHY -> maybeAdminHierarchies
      )
        .withFilter { case (_, maybeContent) => maybeContent.isDefined }
        .map(_._1)
        .reduceLeft((leftContentType, rightContentType) => leftContentType.or(rightContentType))

      val displayNameLayer = new DisplayNameLayer()
      displayNameLayer.setShift(displayCoordinateShift)
      displayNameLayer.setContent(layerContentType)

      maybeAreaNames.foreach(displayNameLayer.setDisplayAreaAttributeMaps)
      maybeLineNames.foreach(displayNameLayer.setDisplayLineRangeAttributeMaps)
      maybePointNames.foreach(displayNameLayer.setDisplayPointAttributeMaps)
      maybeAdminHierarchies.foreach(displayNameLayer.setAdminHierarchyElementDefinitions)

      SuccessResult(Array((displayNameFileKey(data.displayTile.tileId), SerializeUtil.serializeToBytes(displayNameLayer))))
    } else {
      // no name content - compilations still passes without errors but no output payload is produced for the tile
      SuccessResult(Array.empty)
    }
  } match {
    case Success(displayNameTile) => Right(displayNameTile)
    case Failure(exception) =>
      logger.error(s"Names gathering for display features in tile ${data.displayTile.tileId} has failed:", exception)
      Left(FailedResult(exception.getMessage, data))
  }

  private def buildDisplayAreaNames(areas: Seq[DisplayArea],
                                    adminTileBuilder: NdsAdminTileBuilder): Option[NameDisplayAreaAttributeMapList] = {
    val namesAttrMaps: Array[NameDisplayAreaAttributeMap] = areaNameBuilders.flatMap(_.buildAttributes(areas)).toArray
    if (namesAttrMaps.isEmpty) {
      return None
    }
    val namesWithAdminAttrMaps = NdsAreaNameAdminBuilder
      .buildAttributes(areas, adminTileBuilder)
      .map(nameAdminHierarchy => namesAttrMaps :+ nameAdminHierarchy)
      .getOrElse(namesAttrMaps)
    val header = new NameDisplayAreaAttributeMapListHeader(namesWithAdminAttrMaps.length.toShort)
    header.setAttributeTypeCode(namesWithAdminAttrMaps.map(_.getAttributeTypeCode))
    header.setConditionType(
      namesWithAdminAttrMaps.map(attrMap => new ConditionTypeCodeCollection(
        attrMap.getAttributeConditions.flatMap(_.getConditionList).map(_.getConditionTypeCode).distinct
      ))
    )
    val nameMapList = new NameDisplayAreaAttributeMapList(displayCoordinateShift)
    nameMapList.setHeader(header)
    nameMapList.setNumMaps(namesWithAdminAttrMaps.length)
    nameMapList.setMaps(namesWithAdminAttrMaps)
    Some(nameMapList)
  }

  private def buildDisplayLineNames(lines: Seq[DisplayLine],
                                    adminTileBuilder: NdsAdminTileBuilder): Option[NameDisplayLineRangeAttributeMapList] = {
    val namesAttrMaps: Array[NameDisplayLineRangeAttributeMap] = lineNameBuilders.flatMap(_.buildAttributes(lines)).toArray
    if (namesAttrMaps.isEmpty) {
      return None
    }
    val namesWithAdminAttrMaps = NdsLineNameAdminBuilder
      .buildAttributes(lines, adminTileBuilder)
      .map(nameAdminHierarchy => namesAttrMaps :+ nameAdminHierarchy)
      .getOrElse(namesAttrMaps)
    val header = new NameDisplayLineRangeAttributeMapListHeader(namesWithAdminAttrMaps.length.toShort)
    header.setAttributeTypeCode(namesWithAdminAttrMaps.map(_.getAttributeTypeCode))
    header.setConditionType(
      namesWithAdminAttrMaps.map(attrMap => new ConditionTypeCodeCollection(
        attrMap.getAttributeConditions.flatMap(_.getConditionList).map(_.getConditionTypeCode).distinct
      ))
    )
    val nameMapList = new NameDisplayLineRangeAttributeMapList(displayCoordinateShift)
    nameMapList.setHeader(header)
    nameMapList.setNumMaps(namesWithAdminAttrMaps.length)
    nameMapList.setMaps(namesWithAdminAttrMaps)
    Some(nameMapList)
  }

  private def buildDisplayPointNames(points: Seq[DisplayPoint]): Option[NameDisplayPointAttributeMapList] = {
    val pointNamesAttrMaps: Array[NameDisplayPointAttributeMap] = pointNameBuilders.flatMap(_.buildAttributes(points)).toArray
    if (pointNamesAttrMaps.isEmpty) {
      return None
    }
    val header = new NameDisplayPointAttributeMapListHeader(pointNamesAttrMaps.length.toShort)
    header.setAttributeTypeCode(pointNamesAttrMaps.map(_.getAttributeTypeCode))
    header.setConditionType(
      pointNamesAttrMaps.map(attrMap => new ConditionTypeCodeCollection(
        attrMap.getAttributeConditions.flatMap(_.getConditionList).map(_.getConditionTypeCode).distinct
      ))
    )
    val nameMapList = new NameDisplayPointAttributeMapList(displayCoordinateShift)
    nameMapList.setHeader(header)
    nameMapList.setNumMaps(pointNamesAttrMaps.length)
    nameMapList.setMaps(pointNamesAttrMaps)
    Some(nameMapList)
  }
}

object NdsDisplayNameConverter {
  def apply(conf: NdsLiveConfig, env: NdsNameEnvironment) = new NdsDisplayNameConverter(conf, env)

  val areaNameBuilders: Set[NdsDisplayAreaNameBuilder] = Set(
    NdsAreaLabelNameBuilder, NdsAdminAreaNameBuilder, NdsWaterAreaNameBuilder, NdsNatureAreaNameBuilder, NdsBuildingNameBuilder
  )
  val lineNameBuilders: Set[NdsDisplayLineNameBuilder] = Set(NdsWaterLineNameBuilder, NdsLineLabelNameBuilder)
  val pointNameBuilders: Set[NdsDisplayPointNameBuilder] = Set(NdsAdminCenterNameBuilder, NdsMountainPeakNameBuilder)
}