package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.RoadPhysicalWidthMetricExtractor
import com.intellias.osm.compiler.road.common.range.RoadRangeComplete
import com.intellias.osm.compiler.schema.model.road.PhysicalWidth
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.NdsRoad
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.core.attributemap.{Condition, ConditionList}
import play.api.libs.json.Json

object RoadPhysicalWidthBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    topologies.flatMap { ndsRoad =>
        ndsRoad.tags
          .get(RoadPhysicalWidthMetricExtractor.tag)
          .map{json =>
            Json.parse(json).as[Seq[PhysicalWidth]].map(ndsRoad -> _.width)
          }
      }.flatten
      .groupBy(_._2)
      .map {
        case (width, topologyAndValue) =>
          buildMap(width, topologyAndValue.map(_._1))
      }
  }

  private def buildMap(widthCm: Int,
                       refs: Array[NdsRoad]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.PHYSICAL_WIDTH_METRIC)
    attributeValue.setMetricRoadWidth(widthCm.toShort)

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.PHYSICAL_WIDTH_METRIC,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]),
      new ConditionList(0.toShort, Array.empty[Condition])
    )


    buildRoadRangeAttributeSetMap(refs.map((_, DirectionType.Both, RoadRangeComplete())), roadTypeFullAttribute)
  }
}
