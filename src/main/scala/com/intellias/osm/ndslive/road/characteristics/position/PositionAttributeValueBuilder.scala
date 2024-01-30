package com.intellias.osm.ndslive.road.characteristics.position

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.mobility.geo.tools.nds.NdsTools.coordRoadShiftByte
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{NdsRoad, RoadPosition}
import com.intellias.osm.ndslive.road.{NdsRoadId, RoadAttributeValueBuilder}
import com.intellias.osm.ndslive.tools.NdsLiveTools
import nds.characteristics.attributes.{CharacsRoadPositionAttributeType, CharacsRoadPositionAttributeValue}
import nds.characteristics.instantiations._
import nds.core.attributemap.{Condition, ConditionList}
import nds.road.reference.types._


trait PositionAttributeValueBuilder extends RoadAttributeValueBuilder {

  protected def getPositions(tags: Map[String, String]): Seq[RoadPosition]
  protected def attrType: CharacsRoadPositionAttributeType
  protected def attributeValue: CharacsRoadPositionAttributeValue
  protected def attributeProperties: Array[CharacsProperty]


  def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadPositionAttributeSetMap] = {
    topologies
      .map(road => (road, getPositions(road.tags)))
      .filter(_._2.nonEmpty) //filter attributes that don't have a stop_line
      .flatMap { // transform to more easier structure for grouping, based on direction
        case (road, roadPositions) =>
          roadPositions.map(roadPosition => (roadPosition.directionType, road.ndsRoadId, roadPosition.coordinate))
      }
      .groupBy(_._1) // group by direction, forward/backward/both(unknown)
      .map {
        case (direction, lines) => buildAttributeMap(direction, lines.map(t => (t._2, t._3)))
      }
  }

  protected def buildAttributeMap(direction: DirectionType,
                                  refsAndPos: Array[(NdsRoadId, Wgs84Coordinate)]): CharacsRoadPositionAttributeSetMap = {
    val roadWithLines: Seq[(NdsRoadId, Array[Wgs84Coordinate])] = refsAndPos
      .groupBy(_._1)
      .map { case (k, v) => (k, v.map(t => t._2)) }
      .toList
      .sortBy(_._1) // sort by roadId.



    val attSet: CharacsRoadPositionAttributeSet = new CharacsRoadPositionAttributeSet(
      1,
      Array(
        new CharacsRoadPositionFullAttribute(
          attrType,
          attributeValue,
          new CharacsPropertyList(attributeProperties.length.toShort, attributeProperties),
          new ConditionList(0.toShort, Array.empty[Condition])
        ))
    )

    // Road positions.
    val positionValidates: Array[RoadPositionValidity] = roadWithLines.map {
      case (_, positions) => toPositionValidity(positions)
    }.toArray

    // Road references.
    val roadReferences: Array[RoadReference] = roadWithLines
      .map { rp =>
        toRef(
          rp._1,
          if (direction == DirectionType.Both) None
          else Option(direction == DirectionType.Forward)
        )
      }.toArray

    // Position attribute SetMap
    val characsRoadPositionAttributeSetMap = new CharacsRoadPositionAttributeSetMap(coordRoadShiftByte)
    characsRoadPositionAttributeSetMap.setFeature(roadReferences.length) // From the Documentation: "Iterator for feature lists. Also defines size of arrays."
    characsRoadPositionAttributeSetMap.setReferences(roadReferences) // road references
    characsRoadPositionAttributeSetMap.setValidities(positionValidates) // Point on the road of that attribute  RoadValidityType=POSITION[RoadPositionChoice]->RoadValidityPosition->Position2D
    characsRoadPositionAttributeSetMap.setAttributeSet(attSet)

    characsRoadPositionAttributeSetMap
  }


  protected def toPositionChoice(coordinate: Wgs84Coordinate): RoadPositionChoice = {
    val roadValidityPosition = new RoadValidityPosition(coordRoadShiftByte)
    roadValidityPosition.setPosition(NdsLiveTools.position2D(coordinate.lonX, coordinate.latY))
    val roadPositionChoice = new RoadPositionChoice(RoadValidityType.POSITION, coordRoadShiftByte)
    roadPositionChoice.setValidityPosition(roadValidityPosition)

    roadPositionChoice
  }

  protected def toPositionValidity(positions: Array[Wgs84Coordinate]): RoadPositionValidity = {
    new RoadPositionValidity(
      coordRoadShiftByte,
      RoadValidityType.POSITION,
      positions.length,
      positions.map(toPositionChoice)
    )
  }
}