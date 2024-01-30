package com.intellias.osm.compiler.road.characteristic

import com.intellias.osm.compiler.attributes.OsmTagsOps._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import TopologySpec._
import com.intellias.osm.model.road.Topology

import scala.language.implicitConversions

class OsmTagsOpsTest extends AnyFlatSpec with Matchers {




  "tag" should "return true if topology contains the specified tag" in {
    val topology: Topology = Map("building" -> "yes")
    topology.tag("building") shouldBe true
    topology.tag("name") shouldBe false
  }

  "tagValue" should "return true if topology contains the specified tag with the specified value" in {
    val topology: Topology = Map("building" -> "yes", "name" -> "John")
    topology.tagValue("building", "yes") shouldBe true
    topology.tagValue("name", "Mary") shouldBe false
  }

  "oneOf with key and values" should "return true if topology contains the specified tag with any of the specified values" in {
    val topology: Topology = Map("building" -> "yes", "name" -> "John")
    topology.oneOf("building", Set("yes", "no")) shouldBe true
    topology.oneOf("name", Set("Mary", "Jane")) shouldBe false
  }

  "oneOf with key and regex values" should "return true if topology contains the specified tag with any of the specified regex values" in {
    val topology: Topology = Map("maxspeed" -> "30zone:traffic=DE:urban", "source:maxspeed" -> "DE:urban")
    topology.oneOfRegex("maxspeed", ".*:urban") shouldBe true
    topology.oneOfRegex("source:maxspeed", ".*:urban") shouldBe true
  }

  "oneOf with tuples" should "return true if topology contains any of the specified tag-value tuples" in {
    val topology: Topology = Map("building" -> "yes", "name" -> "John")
    topology.oneOf(("building", "no"), ("name", "Mary"), ("building", "yes")) shouldBe true
    topology.oneOf(("name", "Mary"), ("name", "Jane")) shouldBe false
  }

  "allOff" should "return true if topology contains all of the specified tag-value tuples" in {
    val topology: Topology = Map("building" -> "yes", "name" -> "John")
    topology.allOff(("building", "yes"), ("name", "John")) shouldBe true
    topology.allOff(("building", "yes"), ("name", "Mary")) shouldBe false
  }
}
