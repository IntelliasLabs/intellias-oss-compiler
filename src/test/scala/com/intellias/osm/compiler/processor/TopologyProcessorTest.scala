package com.intellias.osm.compiler.processor

import com.holdenkarau.spark.testing.DatasetSuiteBase
import com.intellias.osm.common.{Processor, Workflow}
import com.intellias.osm.compiler.datasource.osm.OsmSourceReader
import com.intellias.osm.compiler.topology.{TopologiesTable, TopologyProcessor}
import com.intellias.osm.{CommonConfig, OsmConfig}
import org.apache.spark.sql.SparkSession
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TopologyProcessorTest extends AnyFunSpec with DatasetSuiteBase with Matchers {
  val commonConf: CommonConfig = CommonConfig(13, "DISK_ONLY",Option.empty,Option.empty)
  val osmConf: OsmConfig = OsmConfig(osmInPath = "src/test/resources/topology/splitRoads")

  it("None of the middle nodes in a topology should be a start or end node") {
    implicit val sparkSession: SparkSession = spark

    val results = new Workflow {
      override def processors: Seq[Processor] = Seq(
        OsmSourceReader(commonConf, osmConf),
        TopologyProcessor(commonConf)
      )
    }.run()

    val topologies = results(TopologiesTable).collect()
    val startEndNodes = topologies.flatMap(t => Seq(t.nodes.head.nodeId, t.nodes.last.nodeId)).toSet
    val middleNodes = topologies.flatMap(t => t.nodes.drop(1).dropRight(1).map(_.nodeId)).toSet

    middleNodes.forall(node => !startEndNodes(node)) shouldBe true
  }
}
