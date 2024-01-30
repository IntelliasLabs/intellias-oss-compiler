package com.intellias.osm.compiler.road

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.SharedProcessorData
import com.intellias.osm.compiler.datasource.osm.{OsmRelation, OsmWay}
import com.intellias.osm.compiler.geometry.{AreaPolygonCollector, multiPolygonToWktUdf}
import com.intellias.osm.compiler.road.RoadAreaCollector.nodesToWKTLineString
import com.intellias.osm.compiler.topology.TopologiesTable
import com.intellias.osm.model.road.TopologyNode
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, SparkSession}
import org.locationtech.jts.geom.{Coordinate, GeometryFactory}

class RoadAreaCollector(data: SharedProcessorData, commonConfig: CommonConfig)(implicit spark: SparkSession) extends Serializable {

  import spark.implicits._

  spark.udf.register("nodesToWKTLineString", nodesToWKTLineString)
  spark.udf.register("areaToWktPolygon", multiPolygonToWktUdf)

  def collect(wayFilter: OsmWay => Boolean, relFilter: OsmRelation => Boolean): Dataset[TopologyWithArea] = {
    val topologies = data(TopologiesTable)

    val areas = new AreaPolygonCollector(data, commonConfig).collect(wayFilter, relFilter, withTags = true)

    areas
      .withColumn("polygon", expr("ST_GeomFromWKT(areaToWktPolygon(area))"))
      .withColumnRenamed("area", "areaGeometry")
      .createOrReplaceTempView("areas_with_geo")

    topologies
      .withColumn("lineString", expr("ST_GeomFromWKT(nodesToWKTLineString(nodes))"))
      .createOrReplaceTempView("topologies_with_geo")

    val roadToArea = spark.sql(
      """select t.topologyId, t.originId, a.areaId, a.derivedFrom, a.tags, a.areaGeometry
        |  from topologies_with_geo t, areas_with_geo a
        | where ST_Intersects(t.lineString, a.polygon)
        |""".stripMargin)

    spark.catalog.dropTempView("areas_witxh_geo")
    spark.catalog.dropTempView("topologies_with_geo")

    val roadAreas = roadToArea
      .groupBy("topologyId")
      .agg(collect_list(struct(
        col("areaId"),
        col("derivedFrom"),
        col("tags"),
        col("areaGeometry")
      )) as "areas")
      .withColumnRenamed("topologyId", "tmpTopologyId")

    topologies
      .join(roadAreas, $"topologyId" === $"tmpTopologyId", "left")
      .select(
        struct($"topologyId", $"originId", $"nodes", $"tags", $"relations", $"tileIds", $"leftAdmin", $"rightAdmin") as "topology",
        coalesce(col("areas"), array()) as "areas"
      )
      .as[TopologyWithArea]
      .persist(commonConfig.sparkStorageLevel)
  }

}

object RoadAreaCollector {
  val nodesToWKTLineString: UserDefinedFunction = udf((nodes: Array[TopologyNode]) => {
    val coordinates = nodes.sortBy(_.nodeIdx).map { case TopologyNode(_, _, lon, lat, _, _, _, _, _, _) => new Coordinate(lon, lat) }
    val polygon = new GeometryFactory().createLineString(coordinates)
    polygon.toText
  })

}

