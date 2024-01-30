package com.intellias.osm.compiler.poi

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.attributes.OsmTagsOps.OsmTagsImplicits
import com.intellias.osm.compiler.datasource.osm.{OsmNodeTable, OsmRelation, OsmWay}
import com.intellias.osm.compiler.geometry.AreaPolygonCollector
import com.intellias.osm.compiler.poi.OsmPoiPreProcessor.isPoiArea
import com.intellias.osm.compiler.{createAreaIdUdf, createNodeIdUdf, polygonCentroidToTileIdUdf, positionToTileIdUdf}
import com.intellias.osm.model.poi.POI
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._

import scala.language.postfixOps

case class OsmPoiPreProcessor(commonConf: CommonConfig) extends Processor with StrictLogging {
  private val toTileUdf = positionToTileIdUdf(commonConf.tileLevel)
  private val toTileAndCenter: UserDefinedFunction = polygonCentroidToTileIdUdf(commonConf.tileLevel)

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val poiFromAreas = new AreaPolygonCollector(data, commonConf)
      .collect(isPoiArea, isPoiArea, withTags = true)
      .filter(_.area.nonEmpty)
      .withColumn("tileAndPosition", toTileAndCenter(col("area")))
      .select(
        createAreaIdUdf($"derivedFrom", $"areaId") as "poiId",
        $"tags",
        $"tileAndPosition.tileId".as("tileId"),
        $"tileAndPosition.longitude".as("longitude"),
        $"tileAndPosition.latitude".as("latitude")
      )

    val nodesWithTile = data(OsmNodeTable)
      .filter(_.tags.nonEmpty)
      .select(
        createNodeIdUdf($"nodeId") as "poiId",
        $"tags",
        toTileUdf($"longitude", $"latitude") as "tileId",
        $"longitude",
        $"latitude"
      )

    val pois = poiFromAreas
      .union(nodesWithTile)
      .select(
        $"poiId",
        $"longitude",
        $"latitude",
        $"tileId",
        $"tags"
      )
      .map(
        r =>
          POI(
            r.getAs("poiId"),
            r.getAs("longitude"),
            r.getAs("latitude"),
            r.getAs("tileId"),
            r.getAs("tags")
        ))
      .persist(commonConf.sparkStorageLevel)


    data + (PoiTable -> pois)
  }
}

object OsmPoiPreProcessor {
  def isPoiArea(way: OsmWay): Boolean = way.tags.oneOf("amenity" -> "charging_station", "amenity" -> "fuel") || way.tags.tag("shop")
  def isPoiArea(relation: OsmRelation): Boolean = false
}