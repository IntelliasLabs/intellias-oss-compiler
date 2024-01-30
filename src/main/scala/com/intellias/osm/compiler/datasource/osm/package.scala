package com.intellias.osm.compiler.datasource

import com.intellias.osm.common.SourceType

import scala.util.{Success, Try}

package object osm {
  case class OsmNode(nodeId: Long, tags: Map[String, String], latitude: Double, longitude: Double)

  case class OsmWay(wayId: Long, tags: Map[String, String], nodeIds: Array[Long])

  case class OsmRelation(relationId: Long, `type`: String, tags: Map[String, String], members: Array[OsmMember])

  case class OsmTag(key: String, value: String)

  case class OsmNodeRef(index: Int, nodeId: Long)

  case class OsmMember(id: Long, role: String, `type`: String)

  case object OsmNodeTable extends SourceType[OsmNode]

  case object OsmWayTable extends SourceType[OsmWay]

  case object OsmRelationTable extends SourceType[OsmRelation]
}
