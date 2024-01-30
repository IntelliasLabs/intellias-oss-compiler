package com.intellias.osm.compiler.display

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.attributes.OsmTagsOps.OsmTagsImplicits
import com.intellias.osm.compiler.datasource.osm.{OsmRelation, OsmWay}
import com.intellias.osm.compiler.display.BuildingFootprintsProcessor.isBuilding
import com.intellias.osm.compiler.geometry.{AreaPolygonCollector, isValidAreaPolygonUdf}
import com.intellias.osm.compiler.{createAreaIdUdf, polygonCentroidToTileIdUdf}
import com.intellias.osm.model.admin.FeatureAdminPlace
import com.intellias.osm.model.display.DisplayArea
import com.intellias.osm.model.display.DisplayFeatureType.BuildingFootprint
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, lit, typedLit}

import scala.language.implicitConversions

class BuildingFootprintsProcessor(config: CommonConfig) extends Processor {
  private val buildingToTileUdf = polygonCentroidToTileIdUdf(config.tileLevel)

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val footprints = new AreaPolygonCollector(data, config)
      .collect(isBuilding, isBuilding, withTags = true)
      .withColumn("tileId", buildingToTileUdf(col("area")).getField("tileId"))
      .where(isValidAreaPolygonUdf(col("area")))
      .select(
        col("tileId"),
        lit(-1).as("localId"),
        createAreaIdUdf(col("derivedFrom"), col("areaId")).as("originalId"),
        col("area").as("geometry"),
        typedLit[String](BuildingFootprint.name) as "featureType",
        col("tags"),
        typedLit[List[FeatureAdminPlace]](List.empty[FeatureAdminPlace]) as "adminPlaces"
      )
      .as[DisplayArea]

    data + (BuildingFootprintTable -> footprints)
  }
}

object BuildingFootprintsProcessor {
  def apply(config: CommonConfig) = new BuildingFootprintsProcessor(config)

  def isBuilding(way: OsmWay): Boolean = way.tags.tag("building")
  def isBuilding(relation: OsmRelation): Boolean = relation.tags.tag("building")
}