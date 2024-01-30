package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.RoadHasStreetLightsExtractor
import com.intellias.osm.compiler.road.common.range.RoadRangeComplete
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{HasStreetLights, NdsRoad}
import com.intellias.osm.ndslive.common.NdsTimeConditionBuilder
import nds.characteristics.attributes.CharacsRoadRangeAttributeType.HAS_STREET_LIGHTS
import nds.characteristics.attributes.CharacsRoadRangeAttributeValue
import nds.characteristics.instantiations._
import nds.core.attributemap.ConditionList
import nds.core.types.Flag
import play.api.libs.json.Json

object RoadHasStreetLightsBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    val roadsWithConditions: Array[(NdsRoad, ConditionList)] = topologies.flatMap { ndsRoad =>
      ndsRoad.tags
        .get(RoadHasStreetLightsExtractor.tag)
        .map { json =>
          ndsRoad -> NdsTimeConditionBuilder.toConditionList(Json.parse(json).as[Seq[HasStreetLights]].flatMap(_.timeConditions))
        }
    }

    roadsWithConditions
      .groupMap(_._2)(_._1)
      .map {
        case (conditionList, refs) =>
          buildMap(conditionList, refs)
      }
  }

  private def buildMap(conditionList: ConditionList, refs: Array[NdsRoad]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(HAS_STREET_LIGHTS)
    attributeValue.setHasStreetLights(new Flag())

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(HAS_STREET_LIGHTS,
                                                                  attributeValue,
                                                                  new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]),
                                                                  conditionList)

    buildRoadRangeAttributeSetMap(refs.map((_, DirectionType.Both, RoadRangeComplete())), roadTypeFullAttribute)
  }
}
