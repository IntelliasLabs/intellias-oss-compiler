package com.intellias.osm.compiler.display

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.datasource.osm.OsmNodeTable
import com.intellias.osm.compiler.display.DisplayPointsProcessor.wrapCoordinatesUdf
import com.intellias.osm.compiler.{createNodeIdUdf, positionToTileIdUdf}
import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.common.wrapper.CoordinateWrapper
import com.intellias.osm.model.display.DisplayPoint
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.{UserDefinedFunction, Window}
import org.apache.spark.sql.functions.{col, lit, row_number, typedlit, udf}

class DisplayPointsProcessor(config: CommonConfig, env: DisplayEnvironment) extends Processor with DisplayFeatureOps with StrictLogging {
  override lazy val knownFeaturesProperties: Array[conf.DisplayFeatureProperties] = env.displayPropertiesService.displayPropertiesList.points

  private val getNodeTile: UserDefinedFunction = positionToTileIdUdf(config.tileLevel)

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._
    val displayPoints = data(OsmNodeTable)
      .filter(node => isSupportedFeatureType(node.tags))
      .select(
        col("nodeId"),
        createNodeIdUdf(col("nodeId")) as "originalId",
        wrapCoordinatesUdf(col("longitude"), col("latitude")) as "geometry",
        col("tags"),
        classifyFeatureUdf(col("tags")) as "featureType",
        getNodeTile(col("longitude"), col("latitude")) as "tileId",
        typedlit[Option[FeatureAdminPlace]](Option.empty[FeatureAdminPlace]) as "adminPlace"
      )
      .withColumn("localId", row_number().over(Window.partitionBy(col("tileId")).orderBy(col("nodeId"))))
      .drop(col("nodeId"))
      .as[DisplayPoint]
      .persist(config.sparkStorageLevel)

    data + (DisplayPointTable -> displayPoints)
  }
}

object DisplayPointsProcessor {
  def apply(config: CommonConfig, env: DisplayEnvironment) = new DisplayPointsProcessor(config, env)
  val wrapCoordinatesUdf: UserDefinedFunction = udf((lon: Double, lat: Double) => CoordinateWrapper(lon, lat))
}