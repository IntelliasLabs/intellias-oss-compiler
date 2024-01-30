package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.RoadTrafficCalmingExtractor
import com.intellias.osm.compiler.road.common.range.RoadRange
import com.intellias.osm.compiler.road.common.range.RoadRange._
import com.intellias.osm.compiler.schema.model.road.TrafficCalmingType
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{NdsRoad, TrafficCalming}
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.characteristics.types.{TrafficCalming => NdsTrafficCalming}
import nds.core.attributemap.{Condition, ConditionList}
import play.api.libs.json.Json

object RoadTrafficCalmingBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    topologies.flatMap { ndsRoad =>
        ndsRoad.tags
          .get(RoadTrafficCalmingExtractor.tag)
          .map { json =>
            Json.parse(json).as[Seq[TrafficCalming]].map(calming => ndsRoad -> calming)
          }
      }.flatten
      .groupMap { case (_, calming) => calming.calmingType } { case (road, calming) => road -> calming }
      .map {
        case (calmingType, roadAndCalming) =>
          buildMap(
            calmingType,
            roadAndCalming.flatMap { case (road, calming) =>
              calming.ranges.map(range => (road, calming.direction, range.toRoadRange))
            }
          )
      }
  }

  private def buildMap(trafficCalming: TrafficCalmingType,
                       refs: Array[(NdsRoad, DirectionType, RoadRange)]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.TRAFFIC_CALMING)
    attributeValue.setTrafficCalming(toNdsCalmingType(trafficCalming))

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.TRAFFIC_CALMING,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]), // in documentation is marked as optional which means that `null` value can be used here
      new ConditionList(0.toShort, Array.empty[Condition])
    )


    buildRoadRangeAttributeSetMap(refs, roadTypeFullAttribute)
  }

  private def toNdsCalmingType(calmType: TrafficCalmingType): NdsTrafficCalming = calmType match {
    case TrafficCalmingType.Bump | TrafficCalmingType.MiniBumps => NdsTrafficCalming.SPEED_BUMP
    case TrafficCalmingType.Hump => NdsTrafficCalming.SPEED_HUMP
    case TrafficCalmingType.Cushion => NdsTrafficCalming.SPEED_CUSHION
    case TrafficCalmingType.Table => NdsTrafficCalming.SPEED_TABLE
    case _ => NdsTrafficCalming.OTHER
  }
}
