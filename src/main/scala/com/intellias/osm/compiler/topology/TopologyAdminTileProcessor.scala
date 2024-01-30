package com.intellias.osm.compiler.topology

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.admin.AdminPlaceGroundTable
import com.intellias.osm.compiler.geometry.multiPolygonToWktUdf
import com.intellias.osm.compiler.road.RoadEnvironment
import com.intellias.osm.compiler.topology.TopologyAdminTileProcessor.{nodesToWKTLineString, saveAndReadDF}
import com.intellias.osm.model.admin.AdminPlaceGround
import com.intellias.osm.model.road.{Topology, TopologyNode}
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}
import org.locationtech.jts.geom.{Coordinate, GeometryFactory}

case class TopologyAdminTileProcessor(commonConfig: CommonConfig, env: RoadEnvironment) extends Processor {
  val adminSplitter: TopologyAdminSplitter = TopologyAdminSplitter(commonConfig.tileLevel, env)
  val tileSplitter: TopologyTileSplitter = TopologyTileSplitter(commonConfig.tileLevel)

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val topologies: Dataset[Topology] = data(TopologiesTable).as("topologies")

    spark.udf.register("areaToWktPolygon", multiPolygonToWktUdf)
    spark.udf.register("nodesToWKTLineString", nodesToWKTLineString)

    val groundPlaces: Dataset[AdminPlaceGround] = data(AdminPlaceGroundTable).as("grounds")

    groundPlaces.select(
      $"adminPlaceId",
      $"areaId",
      $"isoCountryCode",
      $"isoSubCountryCode",
      $"adminLevel",
      $"area",
      expr("ST_GeomFromWKT(areaToWktPolygon(area))").as("polygon")
    ).createOrReplaceTempView("ground_areas_with_geo")

    topologies
      .withColumn("lineString", expr("ST_GeomFromWKT(nodesToWKTLineString(nodes))"))
      .createOrReplaceTempView("topologies_with_geo")


    val roadsToAdminGeo =
      spark.sql(
        """select t.topologyId, a.adminPlaceId, a.areaId, a.isoCountryCode, a.isoSubCountryCode, a.adminLevel, a.area
          |  from topologies_with_geo t, ground_areas_with_geo a
          | where ST_Intersects(t.lineString, a.polygon)
          |""".stripMargin)

    spark.catalog.dropTempView("ground_areas_with_geo")
    spark.catalog.dropTempView("poi_geo")


    val roadIdToAdmin = roadsToAdminGeo
      .groupBy($"topologyId")
      .agg(collect_list(struct(
        $"adminPlaceId",
        $"areaId",
        $"isoCountryCode",
        $"isoSubCountryCode",
        $"adminLevel",
        $"area"
      )) as "admins").as("roadIdToAdmin")

    val roadsWithAdmins = topologies
      .join(roadIdToAdmin, $"topologies.topologyId" === $"roadIdToAdmin.topologyId", "left")
      .select(
        struct(
          $"topologies.topologyId", $"originId", $"nodes", $"tags", $"relations", $"tileIds", $"leftAdmin", $"rightAdmin"
        ) as "topology",
        coalesce($"admins", typedlit(List.empty[AdminPlaceGround])) as "admins"
      )

    val topologiesWithAdmin =  saveAndReadDF("topologiesWithAdmin"){
      roadsWithAdmins.as[(Topology, List[AdminPlaceGround])]
      .flatMap { case (topology, admins) => adminSplitter.splitByAdmins(topology, admins)}
      .flatMap(topology => tileSplitter.splitByTiles(topology))
      .persist(commonConfig.sparkStorageLevel)
    }.as[Topology]

    data + (TopologiesTable -> topologiesWithAdmin)
  }
}

object TopologyAdminTileProcessor {
  val nodesToWKTLineString: UserDefinedFunction = udf((nodes: Array[TopologyNode]) => {
    val coordinates = nodes.sortBy(_.nodeIdx).map { case TopologyNode(_, _, lon, lat, _, _, _, _, _, _) => new Coordinate(lon, lat) }
    val polygon = new GeometryFactory().createLineString(coordinates)
    polygon.toText
  })


  def saveAndReadDF(tableName: String)(ds: Dataset[?])(implicit spark: SparkSession): DataFrame = {
    ds.write
      .mode(SaveMode.Overwrite)
      .saveAsTable(tableName)

    spark.read.table(tableName)
  }
}
