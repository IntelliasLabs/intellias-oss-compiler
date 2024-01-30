package com.intellias.osm.compiler.display

import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.admin.AdminPlaceGroundTable
import com.intellias.osm.compiler.admin.relation.AdminToPointFeatureAssigner
import com.intellias.osm.model.admin.AdminPlaceGround
import com.intellias.osm.model.common.FeatureRef
import com.intellias.osm.model.common.wrapper.CoordinateWrapper
import com.intellias.osm.model.display.DisplayPoint
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{coalesce, typedlit, udf}

class DisplayPointsAdminProcessor(env: DisplayEnvironment) extends Processor {
  private val toFeatureAdminPlaceUdf: UserDefinedFunction =
    udf((adminPlaces: List[AdminPlaceGround]) => env.adminPlaceService.resolveFeatureAdminPlace(adminPlaces))

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val pointFeatures = data(DisplayPointTable)
      .select(
        $"tileId",
        $"localId",
        $"geometry"
      ).as[FeatureRef[CoordinateWrapper]]

    val pointsAdminPlaces = AdminToPointFeatureAssigner.assign(data(AdminPlaceGroundTable), pointFeatures)

    val displayPointsWithAdmins = data(DisplayPointTable)
      .join(pointsAdminPlaces, $"featureTileId" === $"tileId" && $"featureLocalId" === $"localId", "left")
      .select(
        $"tileId",
        $"localId",
        $"originalId",
        $"geometry",
        $"featureType",
        $"tags",
        toFeatureAdminPlaceUdf(coalesce($"adminPlaces", typedlit(List.empty[AdminPlaceGround]))) as "adminPlace"
      ).as[DisplayPoint]

    data + (DisplayPointTable -> displayPointsWithAdmins)
  }
}

object DisplayPointsAdminProcessor {
  def apply(env: DisplayEnvironment) = new DisplayPointsAdminProcessor(env)
}