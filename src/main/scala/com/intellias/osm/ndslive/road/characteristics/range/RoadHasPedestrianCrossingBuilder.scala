package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.RoadHasPedestrianCrossingExtractor
import com.intellias.osm.compiler.road.common.range.RoadRange
import com.intellias.osm.compiler.road.common.range.RoadRange._
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{NdsRoad, PedestrianCrossing}
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.core.attributemap.{Condition, ConditionList}
import nds.core.types.Flag
import play.api.libs.json.Json

object RoadHasPedestrianCrossingBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    topologies.flatMap { ndsRoad =>
      ndsRoad.tags
        .get(RoadHasPedestrianCrossingExtractor.tag)
        .map { json =>
          Json.parse(json).as[Seq[PedestrianCrossing]].map((r: PedestrianCrossing) => ndsRoad -> r)
        }
    }.flatten
      .groupMap{case(road, _) => road }{case (_, pedestrian) => pedestrian}
      .map {
        case (road, pedestrian) =>
        buildMap(pedestrian.flatMap(p => p.ranges.map(range => (road, DirectionType.Both, range.toRoadRange))))
      }
  }

  private def buildMap(refs: Array[(NdsRoad, DirectionType, RoadRange)]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.PEDESTRIAN_CROSSING)
    attributeValue.setHasSidewalk(new Flag())

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.PEDESTRIAN_CROSSING,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]),
      new ConditionList(0.toShort, Array.empty[Condition])
    )

    buildRoadRangeAttributeSetMap(refs, roadTypeFullAttribute)
  }
}
