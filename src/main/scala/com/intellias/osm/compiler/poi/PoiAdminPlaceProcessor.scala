package com.intellias.osm.compiler.poi

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.admin.AdminPlaceGroundTable
import com.intellias.osm.compiler.geometry.{multiPolygonToWktUdf, pointToWKTPointString}
import com.intellias.osm.model.admin.AdminPlaceGround
import com.intellias.osm.model.poi.POI
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

case class PoiAdminPlaceProcessor(commonConf: CommonConfig, env: PoiEnvironment) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._
    spark.udf.register("areaToWktPolygon", multiPolygonToWktUdf)
    spark.udf.register("poiToWKTPointString", pointToWKTPointString)

    val groundPlaces: Dataset[AdminPlaceGround] = data(AdminPlaceGroundTable).as("grounds")
    val pois: Dataset[POI]                      = data(PoiTable).as("pois")

    groundPlaces.select(
      $"adminPlaceId",
      $"areaId",
      $"isoCountryCode",
      $"isoSubCountryCode",
      $"adminLevel",
      $"area",
      expr("ST_GeomFromWKT(areaToWktPolygon(area))").as("polygon")
    ).createOrReplaceTempView("ground_areas_with_geo")

    pois
      .select(
        expr("ST_GeomFromWKT(poiToWKTPointString(longitude,latitude))").as("point"),
        $"poiId"
      )
      .createOrReplaceTempView("poi_geo")

    val poiToAdminIdGeo: DataFrame =
      spark.sql("""select poi.poiId, ground.adminPlaceId, ground.areaId, ground.isoCountryCode, ground.isoSubCountryCode,ground.adminLevel, ground.area
          |  from ground_areas_with_geo ground, poi_geo poi
          | where ST_Contains(ground.polygon, poi.point)
          |""".stripMargin).as("poiToAdminIdGeo")


    val poiIdToAdmins = poiToAdminIdGeo
      .groupBy("poiId")
      .agg(collect_list(struct(
        $"adminPlaceId",
        $"areaId",
        $"isoCountryCode",
        $"isoSubCountryCode",
        $"adminLevel",
        $"area"
      )) as "admins"
      ).as("poiIdToAdmins")

    val poiWithAdmins = pois
      .join(poiIdToAdmins, $"pois.poiId" === $"poiIdToAdmins.poiId", "left")
      .select(
        struct(
          $"pois.poiId", $"pois.longitude", $"pois.latitude", $"pois.tileId", $"pois.tags", $"pois.ndsId", $"pois.categories",
          $"pois.children", $"pois.parents", $"pois.adminPlace"
        ) as "poi",
        coalesce($"admins", typedlit(List.empty[AdminPlaceGround])) as "admins"
      )
      .select($"poi", $"admins")
      .as[(POI, List[AdminPlaceGround])]
      .map {
        case (poi, admins) =>
          poi.copy(adminPlace = env.adminPlaceService.resolveFeatureAdminPlace(admins))
      }

    spark.catalog.dropTempView("ground_areas_with_geo")
    spark.catalog.dropTempView("poi_geo")

    data + (PoiTable -> poiWithAdmins)
  }
}

object PoiAdminPlaceProcessor {


}
