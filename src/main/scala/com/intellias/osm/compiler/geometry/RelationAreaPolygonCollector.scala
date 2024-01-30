package com.intellias.osm.compiler.geometry

import com.intellias.osm.common.SharedProcessorData
import com.intellias.osm.compiler.datasource.osm.{OsmMember, OsmNodeTable, OsmRelation, OsmWayTable}
import com.intellias.osm.compiler.geometry.AreaPolygonCollector.RelationType
import com.intellias.osm.model.common.wrapper.OrderedCoordinateWrapper
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, SparkSession}

class RelationAreaPolygonCollector(data: SharedProcessorData)(implicit spark: SparkSession)
  extends AreaCollector with LineCollector with Serializable with StrictLogging {
  def collect(relations: Dataset[OsmRelation], withTags: Boolean = false): Dataset[AreaPolygon] = {
    import spark.implicits._

    val boundaryLines = collectWayLines(data(OsmWayTable), data(OsmNodeTable))

    relations
      .select(
        col("relationId").as("areaId"),
        explode(col("members")) as "member",
        when(lit(withTags), col("tags")).otherwise(map()).as("relTags")
      )
      .where(isBoundaryWay(col("member")))
      .withColumn("boundaryWayId", col("member.id"))
      .withColumn("memberRole", col("member.role"))
      .drop(col("member"))
      .join(boundaryLines, col("boundaryWayId") === col("wayLineId"))
      .drop("wayLineId", "boundaryWayId")
      .groupBy(col("areaId"))
      // custom aggregator was not used here by intention because it can re-arrange aggregated data and then merge
      // sub-results and in case of coordinates collection to line string it can cause invalid geometries
      .agg(
        collect_list(struct(col("coordinates"), col("memberRole"))) as "boundaryLines",
        first(col("relTags")).as("tags")
      )
      .select(
        col("areaId"),
        collectBoundaryFromPieces(col("boundaryLines")) as "area",
        col("tags"),
        lit(RelationType) as "derivedFrom"
      )
      .as[AreaPolygon]
  }

  val collectBoundaryFromPieces: UserDefinedFunction = udf((pieces: Seq[(Seq[OrderedCoordinateWrapper], String)]) =>
    createPolygon(pieces))

  val isBoundaryWay: UserDefinedFunction = udf((adminMember: OsmMember) =>
    adminMember.`type`.contains("Way") && (adminMember.role.contains("outer") || adminMember.role.contains("inner")))
}
