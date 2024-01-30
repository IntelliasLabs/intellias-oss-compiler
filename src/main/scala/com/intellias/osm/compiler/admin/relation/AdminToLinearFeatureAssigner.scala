package com.intellias.osm.compiler.admin.relation

import com.intellias.mobility.geo.tools.common.Side
import com.intellias.mobility.geo.tools.jts.LineToPolygonSideDetector
import com.intellias.mobility.geo.tools.jts.all.MultiPolygonImplicits
import com.intellias.osm.compiler.geometry.lineToWktUdf
import com.intellias.osm.model.admin.{AdminPlaceGround, FeatureRefAdminPlaceSideAware}
import com.intellias.osm.model.common.FeatureRef
import com.intellias.osm.model.common.wrapper.LineWrapper
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{expr, udf}
import org.apache.spark.sql.{Column, DataFrame, Dataset, SparkSession}

object AdminToLinearFeatureAssigner extends AdminToFeatureAssigner[LineWrapper] {
  override protected val featureToAdminJoinExpr: Column = expr("ST_Intersects(polygon, featureGeometry)")
  private val collectLeftAdminsUdf: UserDefinedFunction = udf(
    (featureGeometry: LineWrapper, adminPlaces: List[AdminPlaceGround]) => collectAdminsForSide(featureGeometry, adminPlaces, Side.LeftSide)
  )
  private val collectRightAdminsUdf: UserDefinedFunction = udf(
    (featureGeometry: LineWrapper, adminPlaces: List[AdminPlaceGround]) => collectAdminsForSide(featureGeometry, adminPlaces, Side.RightSide)
  )

  override protected def provideFeaturesWithGeometries(features: Dataset[FeatureRef[LineWrapper]])
                                                      (implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    spark.udf.register("lineToWktLineString", lineToWktUdf)

    features
      .select(
        $"tileId" as "featureTileId",
        $"localId" as "featureLocalId",
        expr("ST_GeomFromWKT(lineToWktLineString(geometry))") as "featureGeometry"
      )
  }

  def assignBySide(groundPlaces: Dataset[AdminPlaceGround],
                   features: Dataset[FeatureRef[LineWrapper]])
                  (implicit spark: SparkSession): Dataset[FeatureRefAdminPlaceSideAware] = {
    import spark.implicits._

    assign(groundPlaces, features)
      .join(features, $"featureTileId" === $"tileId" && $"featureLocalId" === $"localId", "inner")
      .select(
        $"featureTileId",
        $"featureLocalId",
        collectLeftAdminsUdf($"geometry", $"adminPlaces") as "leftAdminPlaces",
        collectRightAdminsUdf($"geometry", $"adminPlaces") as "rightAdminPlaces"
      )
      .as[FeatureRefAdminPlaceSideAware]
  }

  private def collectAdminsForSide(featureGeometry: LineWrapper,
                                   adminPlaces: List[AdminPlaceGround],
                                   side: Side): List[AdminPlaceGround] = {
    val featureLineJts = featureGeometry.toJTS
    adminPlaces.filter(ap => {
      val adminPolygon = ap.area.toJTS
      val isLineCrossesAdmin = !adminPolygon.isOnBoundary(featureLineJts)
      val isAdminFullyAtTheSide = adminPolygon.findPolygonAdjacentToLine(featureLineJts)
        .map(adjacentPolygon => LineToPolygonSideDetector.identifyPolygonSideRelativeToLine(featureLineJts, adjacentPolygon))
        .contains(side)
      isLineCrossesAdmin || isAdminFullyAtTheSide
    })
  }
}
