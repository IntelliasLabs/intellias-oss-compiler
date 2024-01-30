package com.intellias.osm.compiler.admin.relation

import com.intellias.osm.compiler.geometry.pointToWKTPointString
import com.intellias.osm.model.common.FeatureRef
import com.intellias.osm.model.common.wrapper.CoordinateWrapper
import org.apache.spark.sql.functions.expr
import org.apache.spark.sql.{Column, DataFrame, Dataset, SparkSession}

object AdminToPointFeatureAssigner extends AdminToFeatureAssigner[CoordinateWrapper] {

  override protected def provideFeaturesWithGeometries(features: Dataset[FeatureRef[CoordinateWrapper]])
                                                      (implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    spark.udf.register("coordinatesToWktPoint", pointToWKTPointString)

    features
      .select(
        $"tileId" as "featureTileId",
        $"localId" as "featureLocalId",
        $"geometry.longitude" as "longitude",
        $"geometry.latitude" as "latitude"
      )
      .withColumn("featureGeometry", expr("ST_GeomFromWKT(coordinatesToWktPoint(longitude,latitude))"))
  }

  override protected val featureToAdminJoinExpr: Column = expr("ST_Contains(polygon, featureGeometry)")
}
