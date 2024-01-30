package com.intellias.osm.ndslive.display

import com.intellias.mobility.geo.tools.nds.NdsTileTools
import com.intellias.osm.{CommonConfig, NdsLiveConfig}
import com.intellias.osm.model.display.DisplayTile
import com.intellias.osm.ndslive.display.geometry.{DisplayAreaGeometriesCollector, DisplayLineGeometriesCollector, DisplayPointGeometriesCollector, SimplePolygonSetsWrapper, SimplePolygonWrapper}
import com.intellias.osm.ndslive.tools.NdsLiveTools.CoordinateOps
import com.intellias.osm.ndslive.{FailedResult, NdsConverter, SuccessResult}
import com.typesafe.scalalogging.StrictLogging
import nds.display.layer.{Display2DGeometryLayerList, Display2DLayer, Display2DLayerHeader}
import nds.display.types.{ClippingEdge, ClippingEdgeList}
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.util.LineStringExtracter
import zserio.runtime.io.SerializeUtil

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

class NdsDisplayConverter(ndsLiveConf: NdsLiveConfig,
                          commonConf: CommonConfig) extends NdsConverter[DisplayTile] with StrictLogging with Serializable {
  private def displayFileKey(tileId: Int) = s"${ndsLiveConf.displayPrefix}$tileId"

  override def convert(displayTile: DisplayTile): Either[FailedResult[DisplayTile], SuccessResult] = Try {
    SuccessResult(
      Array((displayFileKey(displayTile.tileId), SerializeUtil.serializeToBytes(buildDisplayLayer(displayTile))))
    )
  } match {
    case Success(result) => Right(result)
    case Failure(exception) =>
      logger.error(s"Display tile ${displayTile.tileId} can't be built", exception)
      Left(FailedResult(exception.getMessage, displayTile))
  }

  private def buildDisplayLayer(displayTile: DisplayTile) = {
    val header = new Display2DLayerHeader()
    val geometriesList = buildGeometriesList(displayTile)
    val clippingEdges = collectClippingEdges(displayTile)

    header.setContent(geometriesList.getContent)
    header.setHasScaleSublevels(false) // TODO: implement
    header.setHasClippingEdgeList(clippingEdges.nonEmpty)

    val displayLayer = new Display2DLayer()
    displayLayer.setHeader(header)
    displayLayer.setGeometryLayers(geometriesList)
    if (clippingEdges.nonEmpty) {
      displayLayer.setClippingEdgeList(
        new ClippingEdgeList(displayCoordinateShift, clippingEdges.length, clippingEdges)
      )
    }
    displayLayer
  }

  private def buildGeometriesList(displayTile: DisplayTile) = {
    // large areas like sea/ocean or forestry might be triangulated; in that case these areas should be distinguished
    // from others (consider passing a predicated to the below function) and set via dedicated method - etAreaDisplay...
    val geometryCollectors = Seq(
      new DisplayAreaGeometriesCollector(displayTile.buildingFootprints) with SimplePolygonWrapper, // Building Footprints
      new DisplayAreaGeometriesCollector(displayTile.areas) with SimplePolygonSetsWrapper, // Other non-triangulated areas
      new DisplayLineGeometriesCollector(displayTile.lines),
      new DisplayPointGeometriesCollector(displayTile.points)
    )

    val content = geometryCollectors.flatMap(_.getContentType).reduce((left, right) => left.or(right))
    val geometriesList = new Display2DGeometryLayerList(content)
    geometryCollectors.foreach(_.enrichWithGeometryLayer(geometriesList))
    geometriesList
  }

  private def collectClippingEdges(tile: DisplayTile): Array[ClippingEdge] = {
    import scala.jdk.CollectionConverters._

    val tileBoundary = NdsTileTools.getTileBoundingBox(commonConf.tileLevel, tile.tileId).getExteriorRing
    Option(tile.areas).getOrElse(Seq.empty)
      .map(_.geometry.toJTS)
      .filter(_.touches(tileBoundary))
      .flatMap(areaGeometry => LineStringExtracter.getLines(areaGeometry.intersection(tileBoundary)).asScala)
      .flatMap(linearGeometry => linearGeometry.asInstanceOf[LineString].getCoordinates.sliding(2))
      .collect {
        case Array(left, right) => new ClippingEdge(displayCoordinateShift, left.toNds, right.toNds)
      }.toArray
  }
}
