package com.intellias.osm.compiler.road.rules

import com.intellias.osm.compiler.attributes.AttributeExtractor
import com.intellias.osm.compiler.datasource.osm.{OsmNode, OsmRelation}
import com.intellias.osm.model.road.Topology

trait RoadRulesExtendedExtractor[T] extends AttributeExtractor[T, OsmRelation] with Serializable {
  def resolveDuplicates(tags: Seq[String]): String

  def getAnchorNode(relation: OsmRelation): Option[Long]

  def decodeRelation( topology: Topology, relation: OsmRelation, nodes: Seq[OsmNode]): Option[T]

  def filterRelation(relation: OsmRelation): Boolean

}
