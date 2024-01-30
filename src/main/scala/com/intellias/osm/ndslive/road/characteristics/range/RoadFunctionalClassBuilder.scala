package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.FunctionalClassExtractor
import com.intellias.osm.compiler.road.common.range.RoadRangeComplete
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{NdsRoad, RoadClass}
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations.{CharacsProperty, CharacsPropertyList, CharacsRoadRangeAttributeSetMap, CharacsRoadRangeFullAttribute}
import nds.core.attributemap.{Condition, ConditionList}
import play.api.libs.json.Json

object RoadFunctionalClassBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    topologies
      .flatMap { ndsRoad =>
        ndsRoad.tags
          .get(FunctionalClassExtractor.tag)
          .map(json => Json.parse(json).as[Seq[RoadClass]].map(ndsRoad -> _))
      }.flatten
      .groupBy(_._2)
      .map { case (roadClass, roads) => buildMap(roadClass.classNum, roads.map(_._1))}
  }

  private def buildMap(roadClass: Int, refs: Array[NdsRoad]): CharacsRoadRangeAttributeSetMap = {
    val functionalClassAttributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.FUNCTIONAL_ROAD_CLASS)
    functionalClassAttributeValue.setFunctionalRoadClass(roadClass.toByte)

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.FUNCTIONAL_ROAD_CLASS,
      functionalClassAttributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]), // in documentation is marked as optional which means that `null` value can be used here
      new ConditionList(0.toShort, Array.empty[Condition]) // in documentation is marked as optional which means that `null` value can be used here
    )

    buildRoadRangeAttributeSetMap(refs.map((_, DirectionType.Both, RoadRangeComplete())), roadTypeFullAttribute)
  }
}
