package com.intellias.osm.compiler.datasource.osm

import com.intellias.mobility.geo.tools.nds.NdsTileTools
import com.intellias.osm.common._
import com.intellias.osm.compiler.datasource.osm.OsmSourceReader._
import com.intellias.osm.{CommonConfig, OsmConfig}
import de.rondiplomatico.nds.NDSTile
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, SparkSession}
import org.locationtech.jts.geom.{Coordinate, GeometryFactory, Polygon}

import scala.io.Source

class OsmSourceReader(commonConfig: CommonConfig, osmConfig: OsmConfig) extends Processor with Serializable {

  private val factory = new GeometryFactory()
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val (node, ways, relations) = if (commonConfig.tileBoundingBoxes.isEmpty && commonConfig.tileList.isEmpty) {

      val nodes: Dataset[OsmNode] = spark.read
        .parquet(s"${osmConfig.osmInPath}/*osm.pbf.node.parquet")
        .drop("timestamp", "changeset", "uid", "user_sid", "version")
        .withColumnRenamed("id", "nodeId")
        .withColumn("tags", toTagMap(col("tags")))
        .as[OsmNode]
        .persist(commonConfig.sparkStorageLevel)

      val ways: Dataset[OsmWay] =
        spark.read
          .parquet(s"${osmConfig.osmInPath}/*osm.pbf.way.parquet")
          .drop("version", "timestamp", "changeset", "uid", "user_sid")
          .withColumn("nodeIds", toNodeIds(col("nodes")))
          .withColumn("tags", toTagMap(col("tags")))
          .withColumnRenamed("id", "wayId")
          .drop("nodes")
          .as[OsmWay]
          .persist(commonConfig.sparkStorageLevel)

      val relations: Dataset[OsmRelation] = spark.read
        .parquet(s"${osmConfig.osmInPath}/*osm.pbf.relation.parquet")
        .drop("version", "timestamp", "changeset", "uid", "user_sid")
        .withColumn("tags", toTagMap(col("tags")))
        .withColumn("type", toRelationType(col("tags")))
        .withColumnRenamed("id", "relationId")
        .as[OsmRelation]
        .persist(commonConfig.sparkStorageLevel)

      (nodes, ways, relations)
    } else {

      val boundingBoxes = createPolygons(commonConfig)

      val nodeFiltered: Dataset[OsmNode] = spark.read
        .parquet(s"${osmConfig.osmInPath}/*osm.pbf.node.parquet")
        .drop("timestamp", "changeset", "uid", "user_sid", "version")
        .withColumnRenamed("id", "nodeId")
        .withColumn("tags", toTagMap(col("tags")))
        .as[OsmNode]
        .filter {
          checkScope(_, boundingBoxes)
        }
        .persist(commonConfig.sparkStorageLevel)

      val allWays = spark.read
        .parquet(s"${osmConfig.osmInPath}/*osm.pbf.way.parquet")
        .drop("version", "timestamp", "changeset", "uid", "user_sid")
        .withColumn("nodeIds", toNodeIds(col("nodes")))
        .withColumn("tags", toTagMap(col("tags")))
        .withColumnRenamed("id", "wayId")
        .drop("nodes")

      val nodeIds = nodeFiltered.map(_.nodeId)

      val wayOutOfScope =
        allWays
          .select($"wayId", explode($"nodeIds") as "wayNodeId")
          .join(nodeIds, $"wayNodeId" === nodeIds("value"), "left_outer")
          .filter(col("value").isNull)
          .select("wayId")
          .withColumnRenamed("wayId", "wayIdToRemove")
          .distinct()

      lazy val waysFiltered: Dataset[OsmWay] = allWays
        .join(wayOutOfScope, allWays("wayId") === wayOutOfScope("wayIdToRemove"), "left_outer")
        .filter(col("wayIdToRemove").isNull)
        .drop("wayIdToRemove")
        .as[OsmWay]
        .persist(commonConfig.sparkStorageLevel)

      val wayIds = waysFiltered.map(_.wayId)

      val allRelations = spark.read
        .parquet(s"${osmConfig.osmInPath}/*osm.pbf.relation.parquet")
        .drop("version", "timestamp", "changeset", "uid", "user_sid")
        .withColumn("tags", toTagMap(col("tags")))
        .withColumn("type", toRelationType(col("tags")))
        .withColumnRenamed("id", "relationId")

      val relationsOutOfScope = allRelations
        .select($"relationId", explode($"members"))
        .drop($"col.role")
        .alias("relationsToRemove")
        .join(nodeIds, $"relationsToRemove.col.id" === nodeIds("value") && $"relationsToRemove.col.type" === "Node".getBytes(), "left_outer")
        .join(wayIds, $"relationsToRemove.col.id" === wayIds("value") && $"relationsToRemove.col.type" === "Way".getBytes(), "left_outer")
        .filter(row => (row.get(2) == null) && (row.get(3) == null))
        .select("relationId")
        .withColumnRenamed("relationId", "relationIdToRemove")
        .distinct()

      val relationsFiltered: Dataset[OsmRelation] = allRelations
        .join(relationsOutOfScope, allRelations("relationId") === relationsOutOfScope("relationIdToRemove"), "left_outer")
        .filter(col("relationIdToRemove").isNull)
        .drop("relationIdToRemove")
        .as[OsmRelation]
        .persist(commonConfig.sparkStorageLevel)
      (nodeFiltered, waysFiltered, relationsFiltered)
    }

    data + (OsmNodeTable -> node) + (OsmWayTable -> ways) + (OsmRelationTable -> relations)
  }

  private def checkScope(osmNode: OsmNode, polygons: Seq[Polygon]): Boolean = {
    if (polygons.isEmpty) {
      true
    } else {
      val point = factory.createPoint(new Coordinate(osmNode.longitude, osmNode.latitude))
      polygons.exists(p => {
        p.contains(point)
      })
    }
  }

  private def createPolygons(commonConfig: CommonConfig): Seq[Polygon] = {
    val rawBBoxes = readConfig(commonConfig.tileBoundingBoxes.getOrElse(""))
      .map(_.split(','))
      .flatMap(list => {
        list.length match {
          case 0 | 1 | 3 => Option.empty
          case 2         =>
            //special case, process it as corner points of squared bounding box
            val array = stringsToCoordinates(list)
            val bbArray =
              Array(array.head, new Coordinate(array.head.getX, array(1).getY), array(1), new Coordinate(array(1).getX, array.head.getY), array.head)
            Option(bbArray)
          case _ => Option(stringsToCoordinates(list))
        }

      })
      .map(factory.createPolygon)

    val tiles = readConfig(commonConfig.tileList.getOrElse("")).map(tileId => {
      val tile = new NDSTile(tileId.toInt)
      NdsTileTools.getTileBoundingBox(tile.getLevel, tile.getTileNumber)
    })

    rawBBoxes ++ tiles
  }

  private def stringsToCoordinates(list: Array[String]) = {
    list.map { p =>
      val doubles = p.trim.split(" ").map(_.toDouble)
      new Coordinate(doubles(0), doubles(1))
    }
  }

  private def readConfig(path: String): Seq[String] = {
    if (path.isEmpty) {
      Seq.empty
    } else {
      val source = Source.fromFile(path)
      val list   = source.getLines.toList
      source.close()
      list
    }
  }
}
object OsmSourceReader {
  def apply(commonConfig: CommonConfig, osmConfig: OsmConfig): OsmSourceReader =
    new OsmSourceReader(commonConfig, osmConfig)

  val toNodeIds: UserDefinedFunction      = udf((nodes: Array[OsmNodeRef]) => nodes.map(_.nodeId))
  val toTagMap: UserDefinedFunction       = udf((tags: Array[OsmTag]) => tags.map(t => t.key -> t.value).toMap)
  val toRelationType: UserDefinedFunction = udf((tags: Map[String, String]) => tags.getOrElse("type", "NA"))
}
