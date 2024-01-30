package com.intellias.osm.ndslive.display

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.display.DisplayEnvironment
import com.intellias.osm.ndslive.display.NdsDisplayMetadataWriter._
import com.intellias.osm.ndslive.display.NdsDisplayTypeMap._
import com.intellias.osm.ndslive.display.order.DrawingOrderProvider
import com.intellias.osm.ndslive.tools.NdsLiveTools.ConfigurableMetaConverters._
import nds.display.metadata._
import nds.display.types.{DisplayAreaType, DisplayLineType, DisplayPointType}
import org.apache.spark.sql.SparkSession
import zserio.runtime.io.SerializeUtil

class NdsDisplayMetadataWriter(ndsConf: NdsLiveConfig, env: DisplayEnvironment) extends Processor with DrawingOrderProvider {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    val layerMeta = new Display2DLayerMetadata()
    layerMeta.setContent(content)
    layerMeta.setAvailableDisplayAreaTypes(areaTypes)
    layerMeta.setAvailableDisplayLineTypes(lineTypes)
    layerMeta.setAvailableDisplayPointTypes(pointTypes)
    layerMeta.setDisplayAreaTypeRelations(areaRelations)
    layerMeta.setDisplayLineTypeRelations(lineRelations)
    layerMeta.setDisplayPointTypeRelations(pointRelations)
    layerMeta.setDefaultDrawingOrders(buildDrawingOrderMapping)
    layerMeta.setDisplayScaleTileLevelMapping(buildScaleTileLevelMapping)

    env.storageServices.foreach(_.save(Iterable((ndsConf.displayMetadata, SerializeUtil.serializeToBytes(layerMeta)))))
    data
  }

  private def buildScaleTileLevelMapping: DisplayScaleTileLevelMapping = {
    val scaleRanges = env.scaleService.scaleRanges
    new DisplayScaleTileLevelMapping(scaleRanges.size.toByte, scaleRanges.map(_.level).toArray, scaleRanges.toArray)
  }

  private def buildDrawingOrderMapping: DrawingOrderMapping = {
    val displayPropertiesList = env.displayPropertiesService.displayPropertiesList
    val (areaTypes, areasOrder) = collectDrawingOrders(displayPropertiesList.areas, NdsDisplayTypeMap.AreaTypeMap).unzip
    val (lineTypes, linesOrder) = collectDrawingOrders(displayPropertiesList.lines, NdsDisplayTypeMap.LineTypeMap).unzip
    val (pointTypes, pointsOrder) = collectDrawingOrders(displayPropertiesList.points, NdsDisplayTypeMap.PointTypeMap).unzip

    val mapping = new DrawingOrderMapping()
    mapping.setNumDisplayAreaTypes(areaTypes.size)
    mapping.setDisplayAreaType(areaTypes.toArray)
    mapping.setDisplayAreaDrawingOrder(areasOrder.toArray)

    mapping.setNumDisplayLineTypes(lineTypes.size)
    mapping.setDisplayLineType(lineTypes.toArray)
    mapping.setDisplayLineDrawingOrder(linesOrder.toArray)

    mapping.setNumDisplayPointTypes(pointTypes.size)
    mapping.setDisplayPointType(pointTypes.toArray)
    mapping.setDisplayPointDrawingOrder(pointsOrder.toArray)
    mapping
  }

}

object NdsDisplayMetadataWriter {
  def apply(ndsConf: NdsLiveConfig, env: DisplayEnvironment) = new NdsDisplayMetadataWriter(ndsConf, env)

  val content: Display2DContent = Display2DContent.Values.SIMPLE_AREAS
    .or(Display2DContent.Values.SIMPLE_AREA_SETS)
    .or(Display2DContent.Values.LINES)
    .or(Display2DContent.Values.POINTS)

  val areaTypes: Array[DisplayAreaType] = DisplayAreaType.AREA_BUILDING +: AreaTypeMap.values.toArray
  val lineTypes: Array[DisplayLineType] = LineTypeMap.values.toArray
  val pointTypes: Array[DisplayPointType] = PointTypeMap.values.toArray

  val areaRelations: Array[DisplayAreaTypeHierarchyRelation] = Array(
    new DisplayAreaTypeHierarchyRelation(DisplayAreaType.DISPLAY_AREA, AreaTypeMap.values.toArray)
  )
  val lineRelations: Array[DisplayLineTypeHierarchyRelation] = Array(
    new DisplayLineTypeHierarchyRelation(DisplayLineType.DISPLAY_LINE, LineTypeMap.values.toArray)
  )
  val pointRelations: Array[DisplayPointTypeHierarchyRelation] = Array(
    new DisplayPointTypeHierarchyRelation(DisplayPointType.DISPLAY_POINT, PointTypeMap.values.toArray)
  )
}