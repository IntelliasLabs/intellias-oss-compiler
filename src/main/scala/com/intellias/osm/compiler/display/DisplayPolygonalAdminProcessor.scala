package com.intellias.osm.compiler.display

import com.intellias.osm.common.{Processor, SharedProcessorData, SourceType}
import com.intellias.osm.compiler.admin.AdminPlaceGroundTable
import com.intellias.osm.compiler.admin.relation.AdminToPolygonalFeatureAssigner
import com.intellias.osm.model.admin.AdminPlaceGround
import com.intellias.osm.model.common.FeatureRef
import com.intellias.osm.model.common.wrapper.MultiPolygonWrapper
import com.intellias.osm.model.display.DisplayArea
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{coalesce, typedlit, udf}

abstract class DisplayPolygonalAdminProcessor(env: DisplayEnvironment) extends Processor {
  private val toFeatureAdminPlacesUdf: UserDefinedFunction =
    udf((adminPlaces: List[AdminPlaceGround]) => env.adminPlaceService.resolveFeatureAdminPlaces(adminPlaces))

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val polygonalFeatures = data(sourceType)
      .select(
        $"tileId",
        $"localId",
        $"geometry"
      ).as[FeatureRef[MultiPolygonWrapper]]

    val areasAdminPlaces = AdminToPolygonalFeatureAssigner.assign(data(AdminPlaceGroundTable), polygonalFeatures)

    val displayAreasWithAdmins = data(sourceType)
      .join(areasAdminPlaces.alias("aa"), $"featureTileId" === $"tileId" && $"featureLocalId" === $"localId", "left")
      .select(
        $"tileId",
        $"localId",
        $"originalId",
        $"geometry",
        $"featureType",
        $"tags",
        toFeatureAdminPlacesUdf(coalesce($"aa.adminPlaces", typedlit(List.empty[AdminPlaceGround]))) as "adminPlaces"
      ).as[DisplayArea]

    data + (sourceType -> displayAreasWithAdmins)
  }

  protected val sourceType: SourceType[DisplayArea]
}

case class BuildingFootprintsAdminProcessor(env: DisplayEnvironment) extends DisplayPolygonalAdminProcessor(env) {
  override protected val sourceType: SourceType[DisplayArea] = BuildingFootprintTable
}

case class DisplayAreasAdminProcessor(env: DisplayEnvironment) extends DisplayPolygonalAdminProcessor(env) {
  override protected val sourceType: SourceType[DisplayArea] = DisplayAreaTable
}