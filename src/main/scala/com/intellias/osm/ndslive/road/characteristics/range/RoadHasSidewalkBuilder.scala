package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.RoadHasSidewalkExtractor
import com.intellias.osm.compiler.road.common.range.{RoadRange, RoadRangeComplete}
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{HasSideWalk, NdsRoad}
import com.intellias.osm.ndslive.common.DirectionDetector
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.core.attributemap.{Condition, ConditionList}
import nds.core.types.Flag
import play.api.libs.json.Json

object RoadHasSidewalkBuilder extends RangeAttributeValueBuilder with Serializable with DirectionDetector {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    val roadWithSideWalk: Array[(NdsRoad, DirectionType, RoadRange)] = topologies.flatMap { ndsRoad =>
        ndsRoad.tags
          .get(RoadHasSidewalkExtractor.tag)
          .map { json =>
            Json.parse(json).as[Seq[HasSideWalk]]
              .map(sideWalk => (ndsRoad, sideToDirection(sideWalk.side), RoadRangeComplete()))
          }
      }.flatten

    Seq(buildMap(roadWithSideWalk))
  }

  private def buildMap(refs: Array[(NdsRoad, DirectionType, RoadRange)]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.HAS_SIDEWALK)
    attributeValue.setHasSidewalk(new Flag())

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.HAS_SIDEWALK,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]),
      new ConditionList(0.toShort, Array.empty[Condition])
    )

    buildRoadRangeAttributeSetMap(refs, roadTypeFullAttribute)
  }
}
