package com.intellias.osm.compiler.road.rules.range

import com.intellias.osm.model.road.{Relation, Topology, TopologyNode, VehicleType}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AccessExtractorTest extends AnyFunSpec with Matchers {

  def createTopology(map: Map[String, String]): Topology = {
    Topology("", 0, Array.empty[TopologyNode], map, List.empty[Relation], Array.empty[Int])
  }

  it("should operate with allowed only cases") {

    val tags = Map(("access", "no"), ("motor_vehicle", "yes"))

    val value = AccessExtractor.decodeFromOsm(createTopology(tags))
    value.head.vehicle.length shouldBe 10
  }

  it("should operate with restricted cases") {

    val tags = Map(("access", "yes"), ("motor_vehicle", "no"))

    val value = AccessExtractor.decodeFromOsm(createTopology(tags))
    value.head.vehicle.length shouldBe 1
    value.head.vehicle.head shouldBe VehicleType.MotorVehicle
  }

  it("should treat 'private' case as 'no'") {

    val tags = Map(("access", "private"), ("motor_vehicle", "yes"))
    val value = AccessExtractor.decodeFromOsm(createTopology(tags))
    value.head.vehicle.length shouldBe 10
  }
}
