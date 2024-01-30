package com.intellias.osm.compiler.road.rules.range

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.mobility.geo.tools.geotools.GeoTools
import com.intellias.osm.compiler.datasource.osm.{OsmNode, OsmRelation}
import com.intellias.osm.compiler.road.rules.RoadRulesExtendedExtractor
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{EnforcementType, EnforcementZone, Topology}
import play.api.libs.json._

// https://wiki.openstreetmap.org/wiki/Relation:enforcement
object EnforcementExtractor extends RoadRulesExtendedExtractor[EnforcementZone] {
  override implicit def writes: Writes[Seq[EnforcementZone]] = Writes.seq
  implicit def reads: Reads[Seq[EnforcementZone]]            = Reads.seq
  implicit val format: Format[EnforcementZone]               = Json.format[EnforcementZone]

  private val keyTag = "enforcement"

  override def tag = "NDS:EnforcementZone"

  override def decodeFromOsm(feature: OsmRelation): Seq[EnforcementZone] = {
    Seq.empty
  }

  override def filterRelation(relation: OsmRelation): Boolean = {
    val isEnforcement = relation.tags.exists { case (tag, value) => tag == "type" && value == keyTag }
    val isStartIdList = relation.members.exists(m => m.role == "from")
    isEnforcement && isStartIdList
  }

  override def decodeRelation(topology: Topology, relation: OsmRelation, nodes: Seq[OsmNode]): Option[EnforcementZone] = {
    val startIdList   = relation.members.filter(m => m.role == "from")
    val membersToList = relation.members.filter(m => m.role == "to")
    val finishIdList = if (membersToList.isEmpty) {
      relation.members.filter(m => m.role == "device")
    } else {
      membersToList
    }

    if (startIdList.isEmpty || finishIdList.isEmpty) {
      Option.empty
    } else {
      val startId = startIdList.head.id
      val finishId = finishIdList.head.id
      val startCoordsList = nodes.filter(_.nodeId == startId).map(node => Wgs84Coordinate(node.latitude, node.longitude))
      val finishCoordsList = nodes.filter(_.nodeId == finishId).map(node => Wgs84Coordinate(node.latitude, node.longitude))
      if (startCoordsList.isEmpty || finishCoordsList.isEmpty) {
        Option.empty
      } else {
        val coords = Array(startCoordsList.head, finishCoordsList.head)

        val seq = topology.nodes.map(_.nodeId).toSeq
        val startIndex = seq.indexOf(startId)
        val finishIndex = seq.indexOf(finishId)

        val directionType = if (startIndex < finishIndex) {
          DirectionType.Forward
        } else {
          if (startIndex > finishIndex) {
            DirectionType.Backward
          } else {
            DirectionType.Both
          }
        }

        val zone = EnforcementZone(EnforcementType.SpeedEnforcementZone, GeoTools.calculateLength(coords).toInt, directionType)
        Option(zone)
      }
    }
  }

  override def getAnchorNode(relation: OsmRelation): Option[Long] = {
    if (filterRelation(relation)) {
      val startIdList = relation.members.filter(m => m.role == "from")

      if (startIdList.isEmpty) {
        None
      } else {
        Option(startIdList.head.id)
      }
    } else {
      None
    }

  }

  private def mergeDirections(dir: DirectionType, dir1: DirectionType): DirectionType = {
    if (dir == dir1) {
      dir
    } else {
      DirectionType.Both
    }
  }

  override def resolveDuplicates(tags: Seq[String]): String = {
    val zones = tags.sortWith(_ < _).flatMap { json =>
      Json.parse(json).as[Seq[EnforcementZone]]
    }

    val zone = zones.tail.foldLeft(zones.head)((zoneA, zoneB) => {
      if (zoneA.enforcementType == zoneB.enforcementType &&
          zoneA.lengthInMeters == zoneB.lengthInMeters) {
        EnforcementZone(zoneA.enforcementType, zoneA.lengthInMeters, mergeDirections(zoneA.direction, zoneB.direction))
      } else {
        zoneA
      }
    })

    Json.toJson(Seq(zone))(writes).toString()
  }
}
