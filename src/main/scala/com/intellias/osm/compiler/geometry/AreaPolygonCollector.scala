package com.intellias.osm.compiler.geometry

import com.intellias.osm.CommonConfig
import com.intellias.osm.common.SharedProcessorData
import com.intellias.osm.compiler.datasource.osm.{OsmRelation, OsmRelationTable, OsmWay, OsmWayTable}
import com.intellias.osm.compiler.geometry.AreaPolygonCollector.isDesiredWayArea
import org.apache.spark.sql.{Dataset, SparkSession}

class AreaPolygonCollector(data: SharedProcessorData, commonConfig: CommonConfig)(implicit spark: SparkSession) {

  def collect(wayFilter: OsmWay => Boolean, relFilter: OsmRelation => Boolean, withTags: Boolean = false): Dataset[AreaPolygon] = {
    import spark.implicits._



    val relations = data(OsmRelationTable).filter(relFilter)
    val ways = data(OsmWayTable).filter(isDesiredWayArea(wayFilter))

    val relationAreas = new RelationAreaPolygonCollector(data).collect(relations, withTags)
      .drop("relationId", "members", "type")

    val wayAreas = new WayAreaCollector(data).collect(ways, withTags)
      .drop("wayId", "nodeIds")


    relationAreas.union(wayAreas)
      .as[AreaPolygon]
      .persist(commonConfig.sparkStorageLevel)
  }
}
object AreaPolygonCollector {
  val WayType = "way"
  val RelationType = "relation"

  val isArea: OsmWay => Boolean = w => w.nodeIds.head == w.nodeIds.last
  def isDesiredWayArea: (OsmWay => Boolean) => OsmWay => Boolean = f => w => isArea(w) && f(w)

}
