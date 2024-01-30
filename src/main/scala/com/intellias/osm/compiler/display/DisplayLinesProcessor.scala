package com.intellias.osm.compiler.display

import com.intellias.mobility.geo.tools.nds.NdsTileTools
import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.createWayIdUdf
import com.intellias.osm.compiler.datasource.osm.{OsmNodeTable, OsmWayTable}
import com.intellias.osm.compiler.display.DisplayLinesProcessor.{hasGeometryUdf, shapePointsToLineUdf}
import com.intellias.osm.compiler.display.conf.DisplayFeatureProperties
import com.intellias.osm.compiler.geometry.LineCollector
import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.common.wrapper.{LineWrapper, OrderedCoordinateWrapper}
import com.intellias.osm.model.display.DisplayLine
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.{UserDefinedFunction, Window}
import org.apache.spark.sql.functions._
import org.locationtech.jts.geom.LineString

class DisplayLineProcessor(config: CommonConfig, env: DisplayEnvironment)
  extends Processor with DisplayFeatureOps with LineCollector with StrictLogging {

  override lazy val knownFeaturesProperties: Array[DisplayFeatureProperties] = env.displayPropertiesService.displayPropertiesList.lines

  private val coveredTilesUdf: UserDefinedFunction = udf(
    (line: LineWrapper) => NdsTileTools.collectCoveredTiles(line.toJTS, config.tileLevel).map(_.toInt)
  )

  private val linePartInTileUdf: UserDefinedFunction = udf((lineId: String, line: LineWrapper, tileId: Int) => {
    val lineJts = line.toJTS
    val tileBBox = NdsTileTools.getTileBoundingBox(config.tileLevel, tileId)
    val linePartInTile = lineJts.intersection(tileBBox)
    linePartInTile match {
      case line: LineString => LineWrapper.fromJTS(line)
      case _ =>
        logger.error(s"Non-linear part of line $lineId belongs to the tile $tileId. Falling back to empty tiled geometry.")
        LineWrapper.empty
    }
  })

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val waysWithGeometries = collectWayLines(data(OsmWayTable), data(OsmNodeTable))

    val displayLines = data(OsmWayTable)
      .filter(way => isSupportedFeatureType(way.tags))
      .alias("ways")
      .join(waysWithGeometries.alias("lines"), $"lines.wayLineId" === $"ways.wayId")
      .drop($"lines.tags")
      .withColumn("wayLineGeometry", shapePointsToLineUdf(col("coordinates")))
      .select(
        col("wayId"),
        createWayIdUdf(col("wayId")) as "originalId",
        col("tags"),
        col("wayLineGeometry"),
        classifyFeatureUdf(col("tags")) as "featureType",
        explode(coveredTilesUdf(col("wayLineGeometry"))) as "tileId",
        typedLit[List[FeatureAdminPlace]](List.empty[FeatureAdminPlace]) as "leftAdminPlaces",
        typedLit[List[FeatureAdminPlace]](List.empty[FeatureAdminPlace]) as "rightAdminPlaces"
      )
      .withColumn("geometryInTile", linePartInTileUdf($"wayId", $"wayLineGeometry", $"tileId"))
      .where(hasGeometryUdf($"geometryInTile"))
      .withColumn("localId", row_number().over(Window.partitionBy($"tileId").orderBy($"wayId")))
      .drop("wayId", "wayLinGeometry")
      .withColumnRenamed("geometryInTile", "geometry")
      .as[DisplayLine]
      .persist(config.sparkStorageLevel)

    data + (DisplayLineTable -> displayLines)
  }


}

object DisplayLinesProcessor {
  def apply(config: CommonConfig, env: DisplayEnvironment) = new DisplayLineProcessor(config, env)

  val hasGeometryUdf: UserDefinedFunction = udf((line: LineWrapper) => !line.isEmpty)
  val shapePointsToLineUdf: UserDefinedFunction =
    udf((wayShapePoints: Array[OrderedCoordinateWrapper]) => LineWrapper(wayShapePoints.toSeq))
}