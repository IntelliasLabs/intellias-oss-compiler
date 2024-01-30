package com.intellias.osm.compiler.display

import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.admin.AdminPlaceGroundTable
import com.intellias.osm.compiler.admin.relation.AdminToLinearFeatureAssigner
import com.intellias.osm.model.admin.AdminPlaceGround
import com.intellias.osm.model.common.FeatureRef
import com.intellias.osm.model.common.wrapper.LineWrapper
import com.intellias.osm.model.display.DisplayLine
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{coalesce, typedlit, udf}

class DisplayLinesAdminProcessor(env: DisplayEnvironment) extends Processor {
  private val toFeatureAdminPlacesUdf: UserDefinedFunction =
    udf((adminPlaces: List[AdminPlaceGround]) => env.adminPlaceService.resolveFeatureAdminPlaces(adminPlaces))

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val linearFeatures = data(DisplayLineTable)
      .select($"tileId", $"localId", $"geometry")
      .as[FeatureRef[LineWrapper]]

    val linesAdminPlaces = AdminToLinearFeatureAssigner.assignBySide(data(AdminPlaceGroundTable), linearFeatures)

    val displayLinesWithAdmins = data(DisplayLineTable)
      .join(linesAdminPlaces.as("lap"), $"featureTileId" === $"tileId" && $"featureLocalId" === $"localId", "left")
      .select(
        $"tileId",
        $"localId",
        $"originalId",
        $"geometry",
        $"featureType",
        $"tags",
        toFeatureAdminPlacesUdf(coalesce($"lap.leftAdminPlaces", typedlit(List.empty[AdminPlaceGround]))) as "leftAdminPlaces",
        toFeatureAdminPlacesUdf(coalesce($"lap.rightAdminPlaces", typedlit(List.empty[AdminPlaceGround]))) as "rightAdminPlaces"
      ).as[DisplayLine]

    data + (DisplayLineTable -> displayLinesWithAdmins)
  }
}

object DisplayLinesAdminProcessor {
  def apply(env: DisplayEnvironment) = new DisplayLinesAdminProcessor(env)
}