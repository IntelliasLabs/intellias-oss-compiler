package com.intellias.osm.compiler.admin

import com.intellias.osm.AdminPlaceConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.model.admin.{AdminPlace, AdminPlaceGround}
import org.apache.spark.sql.SparkSession

case class AdminHierarchyReader(config: AdminPlaceConfig) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._
    val groundAdmins = spark.read
      .parquet(config.adminPlaceGroundOut).as[AdminPlaceGround]

    val adminPlaces = spark.read
      .parquet(config.adminPlaceOut).as[AdminPlace]


    data + (AdminHierarchyTable -> adminPlaces) + (AdminPlaceGroundTable -> groundAdmins)
  }
}
