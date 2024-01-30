package com.intellias.osm.compiler.processor

import com.holdenkarau.spark.testing.DatasetSuiteBase
import com.intellias.osm.common.{Processor, Workflow}
import com.intellias.osm.compiler.datasource.osm.OsmSourceReader
import com.intellias.osm.compiler.road.RoadAreaCharacteristicsProcessor
import com.intellias.osm.compiler.road.characteristic.area.RoadAreaBusinessDistrictExtractor
import com.intellias.osm.compiler.road.characteristic.range.RoadTypeCharsExtractor
import com.intellias.osm.compiler.topology.{TopologiesTable, TopologyProcessor}
import com.intellias.osm.model.road.{RoadCharacterType, RoadCharacteristics, Topology}
import com.intellias.osm.{CommonConfig, OsmConfig}
import org.apache.sedona.sql.utils.SedonaSQLRegistrator
import org.apache.spark.sql.SparkSession
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

import scala.language.postfixOps

class RoadAreaCharacteristicsProcessorTest extends AnyFunSpec with DatasetSuiteBase with Matchers {
  val commonConf: CommonConfig = CommonConfig(13, "DISK_ONLY",Option.empty,Option.empty)
  val osmConf: OsmConfig = OsmConfig(osmInPath = "src/test/resources/topology/area")

  it("should correctly match topologies to areas") {
    implicit val sparkSession: SparkSession = spark

    SedonaSQLRegistrator.registerAll(spark)

    val results = new Workflow {
      override def processors: Seq[Processor] = Seq(
        OsmSourceReader(commonConf, osmConf),
        TopologyProcessor(commonConf),
        RoadAreaCharacteristicsProcessor(commonConf)
      )
    }.run()

    val topologies = results(TopologiesTable).collect()

    val businessRoads = topologies.filter(f => f.tags.contains(RoadAreaBusinessDistrictExtractor.tag))
    val residentialRoads = topologies.filter(isUrbanRoad)

    businessRoads.length shouldBe 5
    residentialRoads.length shouldBe 1
  }

  def isUrbanRoad(topology: Topology): Boolean = {
    topology.tags
      .get(RoadTypeCharsExtractor.tag).exists { json =>
        Json
          .parse(json)
          .as[Seq[RoadCharacteristics]]
          .flatMap(rChars => rChars.characterTypes)
          .contains(RoadCharacterType.Urban)
      }
  }
}
