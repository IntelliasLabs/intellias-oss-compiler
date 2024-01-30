package com.intellias.osm.ndslive.road.characteristics.range

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.mobility.geo.tools.nds.NdsTools.coordRoadShiftByte
import com.intellias.osm.compiler.road.common.range.{PositionRange, RoadPositionRanges, RoadRange, RoadRangeComplete}
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.NdsRoad
import com.intellias.osm.ndslive.road.RoadAttributeValueBuilder
import com.intellias.osm.ndslive.tools.NdsLiveTools
import nds.characteristics.instantiations.{CharacsRoadRangeAttributeSet, CharacsRoadRangeAttributeSetMap, CharacsRoadRangeFullAttribute}
import nds.road.reference.types._

trait RangeAttributeValueBuilder extends RoadAttributeValueBuilder  {
  def buildAttributes(topologies: Array[NdsRoad]): Iterable[CharacsRoadRangeAttributeSetMap]


  def buildRoadRangeAttributeSetMap(refs: Iterable[(NdsRoad, DirectionType, RoadRange)],
                                    roadRangeFullAttribute: CharacsRoadRangeFullAttribute): CharacsRoadRangeAttributeSetMap = {
    // I do not know why did they introduce this `CharacsRoadRangeAttributeSet` as it seems that it will always
    // wrap a single element. My guess is that it is inherited from the NDS Classic where they have primary and
    // secondary attributes. Then such attribute groups are wrapped in the structures like this. But in NDS.Live
    // there are no attribute groups anymore and secondary attributes are modeled with properties and conditions.
    // Anyway, let's populate it as a structure holding a single element.
    val roadRangeAttributeSet = new CharacsRoadRangeAttributeSet(1, Array(roadRangeFullAttribute))

    // Imagine, that there are two dual-carriageway roads in the map having IDs 4 and 5
    // It makes no sense to have directed road references in this particular case therefore those are undirected
    val roadReferences = refs.map { ref =>
      toRef(
        ref._1.ndsRoadId,
        if (ref._2 == DirectionType.Both) None
        else Option(ref._2 == DirectionType.Forward)
      )
    }

    val validityRanges = refs.map(ref => toRoadRangeValidity(ref._3))

    // Maps attribute to the features sharing the same attribute value
    val characsRoadRangeAttributeSetMap = new CharacsRoadRangeAttributeSetMap(coordRoadShiftByte)
    characsRoadRangeAttributeSetMap.setAttributeSet(roadRangeAttributeSet)
    characsRoadRangeAttributeSetMap.setFeature(roadReferences.size) // From the Documentation: "Iterator for feature lists. Also defines size of arrays."
    characsRoadRangeAttributeSetMap.setReferences(roadReferences.toArray)
    characsRoadRangeAttributeSetMap.setValidities(validityRanges.toArray)

    characsRoadRangeAttributeSetMap
  }

  private def toRoadRangeValidity(ranges: RoadRange): RoadRangeValidity = ranges match {
    case RoadRangeComplete() => new RoadRangeValidity(coordRoadShiftByte, RoadValidityType.COMPLETE, 0, null)
    case RoadPositionRanges(ranges) =>
      val validitiRanges = ranges.map{r =>
        val rangChoice = new RoadRangeChoice(RoadValidityType.POSITION, coordRoadShiftByte)
        rangChoice.setValidityRange(toRoadValidityRange(r))
        rangChoice
      }

      new RoadRangeValidity(coordRoadShiftByte,
        RoadValidityType.POSITION,
        validitiRanges.size,
        validitiRanges.toArray)
  }

  private def toRoadValidityRange(range: PositionRange):RoadValidityRange = {
    new RoadValidityRange(coordRoadShiftByte, toRoadValidityPosition(range.start), toRoadValidityPosition(range.end))
  }

  private def toRoadValidityPosition(wgs: Wgs84Coordinate): RoadValidityPosition = {
    val rvp = new RoadValidityPosition(coordRoadShiftByte)
    rvp.setPosition(NdsLiveTools.position2D(wgs.lonX, wgs.latY))
    rvp
  }
}
