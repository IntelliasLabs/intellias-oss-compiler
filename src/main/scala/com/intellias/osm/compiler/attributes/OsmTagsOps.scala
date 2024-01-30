package com.intellias.osm.compiler.attributes

import com.intellias.osm.model.road.{Topology, TopologyNode}

object OsmTagsOps {
  trait TagsOps {
    protected def tags: Map[String, String]

    def tag(key: String): Boolean = tags.contains(key)

    def tagValue(key: String, value: String): Boolean = tags.get(key).contains(value)

    def oneOf(key: String, values: Set[String]): Boolean = tags.get(key).exists(values)

    def oneOf(tuples: (String, String)*): Boolean =
      tuples.exists { case (key, value) => tagValue(key, value) }

    def oneOfRegex(key: String, regexValues: String*): Boolean = tags.get(key).exists(tagVal => regexValues.exists(regV => tagVal.matches(regV)))

    def allOff(tuples: (String, String)*): Boolean = tuples.forall { case (key, value) => tagValue(key, value) }
  }

  implicit class TopologyTagsImplicits(topology: Topology) extends TagsOps {
    override val tags: Map[String, String] = topology.tags
  }

  implicit class TopologyNodeImplicits(node: TopologyNode) extends TagsOps {
    override val tags: Map[String, String] = node.tags
  }

  implicit class OsmTagsImplicits(val tags: Map[String, String]) extends TagsOps {

  }
}
