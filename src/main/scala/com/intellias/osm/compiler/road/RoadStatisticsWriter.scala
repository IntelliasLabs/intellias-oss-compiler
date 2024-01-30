package com.intellias.osm.compiler.road

import com.intellias.osm.RoadConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.road.RoadStatisticsWriter.{getRoadChars, getRoadForm}
import com.intellias.osm.compiler.road.characteristic.range.{RoadTypeCharsExtractor, RoadTypeFormExtractor}
import com.intellias.osm.compiler.topology.TopologiesTable
import com.intellias.osm.model.road.{RoadAndChars, RoadCharacterType, RoadCharacteristics, RoadFormType}
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{col, collect_set, explode, udf}
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}
import play.api.libs.json.Json

case class RoadStatisticsWriter(config: RoadConfig) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val roadFormChars: Dataset[String] = data(TopologiesTable)
      .select(
        getRoadForm(col("tags")) as "roadForm",
        explode(getRoadChars(col("tags"))) as "roadChar"
      )
      .groupBy(col("roadForm"))
      .agg(collect_set(col("roadChar")) as "roadChars")
      .as[(String, Seq[String])]
      .map { case (roadFormStr, roadCharsStr) =>
        val roadForm = Json.parse(roadFormStr).as[RoadFormType]
        val roadChars = roadCharsStr.map(charStr => Json.parse(charStr).as[RoadCharacterType])

        Json.toJson(RoadAndChars(roadForm, roadChars)).toString()
      }

    roadFormChars
      .repartition(1)
      .write
      .mode(SaveMode.Overwrite)
      .parquet(config.statisticPath)

    data
  }
}

object RoadStatisticsWriter extends StrictLogging {

  val getRoadForm: UserDefinedFunction = udf((tags: Map[String, String]) => toFormStr(tags))
  val getRoadChars: UserDefinedFunction = udf((tags: Map[String, String]) => toCharacteristicStr(tags))

  def toFormStr(tags: Map[String, String]): String = {
    val roadForm = tags
        .get(RoadTypeFormExtractor.tag)
        .flatMap(json => Json.parse(json).as[Seq[RoadFormType]].headOption)
        .getOrElse(RoadFormType.Any)

    Json.toJson(roadForm).toString()
  }

  def toCharacteristicStr(tags: Map[String, String]): Seq[String] = {
    tags
      .get(RoadTypeCharsExtractor.tag)
      .map { json =>
        Json
          .parse(json)
          .as[Seq[RoadCharacteristics]]
          .map(roadChars => roadChars.characterTypes.map(Json.toJson(_).toString()))
      }
      .getOrElse(Seq.empty)
      .flatten
  }
}
