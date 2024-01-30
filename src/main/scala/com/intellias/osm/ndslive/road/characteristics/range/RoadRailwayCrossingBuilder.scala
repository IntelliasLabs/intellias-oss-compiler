package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.RoadRailwayCrossingExtractor
import com.intellias.osm.compiler.road.common.range.RoadRange
import com.intellias.osm.compiler.road.common.range.RoadRange._
import com.intellias.osm.model.common.{DirectionType, FeatureRange}
import com.intellias.osm.model.road.RailwayCrossingGateType.{DoubleHalf, Full, Half, NoGates, Other, Unknown}
import com.intellias.osm.model.road.{NdsRoad, RailwayCrossingGate, RailwayCrossingGateType}
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.characteristics.types.{RailwayCrossing => NdsRailwayCrossing}
import nds.core.attributemap.{Condition, ConditionList}
import play.api.libs.json.Json

object RoadRailwayCrossingBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    val bb: Map[NdsRailwayCrossing, Array[(NdsRoad, Seq[FeatureRange])]] = topologies.flatMap { ndsRoad =>
        ndsRoad.tags
          .get(RoadRailwayCrossingExtractor.tag)
          .map { json => Json.parse(json).as[Seq[RailwayCrossingGate]].map(gate => ndsRoad -> gate)}
      }.flatten
      .groupMap{case(road, gate) => toNdsGateType(gate.gateType) }{case (road, gate) => (road, gate.ranges)}

      bb.map { case (ndsGateType, roadAndRanges) =>
        val refs: Array[(NdsRoad, DirectionType, RoadRange)] = roadAndRanges.flatMap { case (road, ranges) =>
          ranges.map(range => (road, DirectionType.Both, range.toRoadRange))
        }

        buildMap(ndsGateType, refs)
      }
  }

  private def buildMap(railwayCrossing: NdsRailwayCrossing, refs: Array[(NdsRoad, DirectionType, RoadRange)]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.RAILWAY_CROSSING)
    attributeValue.setRailwayCrossing(railwayCrossing)

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.RAILWAY_CROSSING,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]), // in documentation is marked as optional which means that `null` value can be used here
      new ConditionList(0.toShort, Array.empty[Condition])
    )

    buildRoadRangeAttributeSetMap(refs, roadTypeFullAttribute)
  }

  private def toNdsGateType(gateType: RailwayCrossingGateType): NdsRailwayCrossing = gateType match {
    case DoubleHalf | Full | Half | Other => NdsRailwayCrossing.RAILROAD_CROSSING_WITH_GATES
    case NoGates => NdsRailwayCrossing.RAILROAD_CROSSING_WITHOUT_GATES
    case Unknown => NdsRailwayCrossing.RAILROAD_CROSSING_UNKNOWN
  }
}
