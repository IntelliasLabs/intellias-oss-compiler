package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.osm.compiler.road.characteristic.range.{FunctionalClassExtractor, RoadPavementExtractor}
import com.intellias.osm.compiler.road.common.range.RoadRangeComplete
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.{NdsRoad, PavementType, RoadClass}
import nds.characteristics.attributes.{CharacsRoadRangeAttributeType, CharacsRoadRangeAttributeValue}
import nds.characteristics.instantiations._
import nds.characteristics.types.{PavementType => NdsPavementType}
import nds.core.attributemap.{Condition, ConditionList}
import play.api.libs.json.Json

object RoadPavementTypeBuilder extends RangeAttributeValueBuilder with Serializable {
  override def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap] = {
    topologies.flatMap { ndsRoad =>
      ndsRoad.tags
        .get(RoadPavementExtractor.tag)
        .map(json => Json.parse(json).as[Seq[PavementType]].map(ndsRoad -> _))
    }.flatten
      .groupBy(_._2)
      .map {
        case (pavementType, topologyAndValue) =>
          buildMap(toNdsType(pavementType), topologyAndValue.map(_._1))
      }
  }

  private def buildMap(pavementType: NdsPavementType, refs: Array[NdsRoad]): CharacsRoadRangeAttributeSetMap = {
    val attributeValue = new CharacsRoadRangeAttributeValue(CharacsRoadRangeAttributeType.TYPE_OF_PAVEMENT)
    attributeValue.setPavementType(pavementType)

    val roadTypeFullAttribute = new CharacsRoadRangeFullAttribute(
      CharacsRoadRangeAttributeType.TYPE_OF_PAVEMENT,
      attributeValue,
      new CharacsPropertyList(0.toShort, Array.empty[CharacsProperty]), // in documentation is marked as optional which means that `null` value can be used here
      new ConditionList(0.toShort, Array.empty[Condition])
    )

    buildRoadRangeAttributeSetMap(refs.map((_, DirectionType.Both, RoadRangeComplete())), roadTypeFullAttribute)
  }

  private def toNdsType(pavementType: PavementType): NdsPavementType = pavementType match {
    case PavementType.Asphalt     => NdsPavementType.ASPHALT
    case PavementType.Sandy       => NdsPavementType.SANDY
    case PavementType.Gravel      => NdsPavementType.GRAVEL
    case PavementType.Cobblestone => NdsPavementType.COBBLESTONE
    case PavementType.Concrete    => NdsPavementType.CONCRETE
    case PavementType.Paved       => NdsPavementType.PAVED
    case PavementType.Other       => NdsPavementType.OTHER
    case PavementType.Unpaved     => NdsPavementType.UNPAVED
    case PavementType.Unknown     => NdsPavementType.UNKNOWN
  }

}
