package com.intellias.osm.compiler.admin.relation

import com.intellias.osm.compiler.geometry.multiPolygonToWktUdf
import com.intellias.osm.model.common.FeatureRef
import com.intellias.osm.model.common.wrapper.MultiPolygonWrapper
import org.apache.spark.sql.functions.expr
import org.apache.spark.sql.{Column, DataFrame, Dataset, SparkSession}

object AdminToPolygonalFeatureAssigner extends AdminToFeatureAssigner[MultiPolygonWrapper] {
  override protected def provideFeaturesWithGeometries(features: Dataset[FeatureRef[MultiPolygonWrapper]])
                                                      (implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    spark.udf.register("areaToWktPolygon", multiPolygonToWktUdf)

    features
      .select(
        $"tileId" as "featureTileId",
        $"localId" as "featureLocalId",
        expr("ST_GeomFromWKT(areaToWktPolygon(geometry))") as "featureGeometry"
      )
  }

  override protected val featureToAdminJoinExpr: Column = expr("ST_Overlaps(polygon, featureGeometry)")
}
