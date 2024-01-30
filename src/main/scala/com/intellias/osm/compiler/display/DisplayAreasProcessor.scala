package com.intellias.osm.compiler.display

import com.intellias.mobility.geo.tools.nds.NdsTileTools
import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.createAreaIdUdf
import com.intellias.osm.compiler.datasource.osm.{OsmRelation, OsmWay}
import com.intellias.osm.compiler.display.conf.DisplayFeatureProperties
import com.intellias.osm.compiler.geometry.{AreaPolygonCollector, isValidAreaPolygonUdf}
import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.common.wrapper.MultiPolygonWrapper
import com.intellias.osm.model.display.{DisplayArea, DisplayFeatureType}
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

class DisplayAreasProcessor(config: CommonConfig, env: DisplayEnvironment) extends Processor with DisplayFeatureOps with StrictLogging {
  override lazy val knownFeaturesProperties: Array[DisplayFeatureProperties] = env.displayPropertiesService.displayPropertiesList.areas

  private val coveredTilesUdf: UserDefinedFunction = udf(
    (area: MultiPolygonWrapper) => Try {
      NdsTileTools.collectCoveredTiles(area.toJTS, config.tileLevel).map(_.toInt)
    } match {
      case Success(tileList) => tileList
      case Failure(exception) =>
        logger.error("Failed to determine list of tiles that area covers:", exception)
        Set.empty[Int]
    }
  )

  private val areaGeometryInTileUdf: UserDefinedFunction = udf((areaId: String, area: MultiPolygonWrapper, tileId: Int) => {
    val areaJts = area.toJTS
    val tileBBox = NdsTileTools.getTileBoundingBox(config.tileLevel, tileId)
    Try {
      if (areaJts.overlaps(tileBBox)) {
        MultiPolygonWrapper.fromJTS(areaJts.intersection(tileBBox)) match {
          case Right(multiPolygon) => multiPolygon
          case Left(exception) =>
            logger.error(s"Failed to collect polygon for area $areaId:", exception)
            MultiPolygonWrapper()
        }
      } else {
        logger.error(s"Area $areaId as recognized as related to tile $tileId but it does not overlap with the tile's bounding box")
        MultiPolygonWrapper()
      }
    } match {
      case Success(multiPolygon) => multiPolygon
      case Failure(error) =>
        logger.error(s"Exception occurred while calculating area $areaId part belonging to the tile $tileId:", error)
        MultiPolygonWrapper()
    }
  })


  private def isSupportedAreaType(osmRelation: OsmRelation): Boolean = isSupportedFeatureType(osmRelation.tags)
  private def isSupportedAreaType(osmWay: OsmWay): Boolean = isSupportedFeatureType(osmWay.tags)

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val displayAreas = new AreaPolygonCollector(data, config)
      .collect(isSupportedAreaType, isSupportedAreaType, withTags = true)
      .select(
        col("areaId"),
        col("tags"),
        col("area"),
        createAreaIdUdf($"derivedFrom", $"areaId").as("originalId"),
        explode(coveredTilesUdf($"area")) as "tileId",
        classifyFeatureUdf($"tags") as "featureType",
        lit(-1).as("localId"),
        typedLit[List[FeatureAdminPlace]](List.empty[FeatureAdminPlace]) as "adminPlaces"
      )
      .where($"featureType" =!= DisplayFeatureType.Unknown.name)
      .withColumn("geometry", areaGeometryInTileUdf($"areaId", $"area", $"tileId"))
      .where(isValidAreaPolygonUdf($"geometry"))
      .drop("area", "areaId")
      .as[DisplayArea]

    data + (DisplayAreaTable -> displayAreas)
  }
}

object DisplayAreasProcessor {
  def apply(config: CommonConfig, env: DisplayEnvironment) = new DisplayAreasProcessor(config, env)
}
