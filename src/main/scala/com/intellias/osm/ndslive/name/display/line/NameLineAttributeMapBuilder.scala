package com.intellias.osm.ndslive.name.display.line

import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.intellias.osm.ndslive.name.AttributeMapBuilder
import nds.core.types.Var4ByteId
import nds.display.reference.types.{DisplayLineRangeValidity, DisplayLineReference, DisplayLineValidityType}
import nds.name.attributes.NameDisplayLineRangeAttributeType
import nds.name.instantiations.NameDisplayLineRangeAttributeMap

trait NameLineAttributeMapBuilder extends AttributeMapBuilder {
  val attributeType: NameDisplayLineRangeAttributeType

  protected def buildAttrMap(lineNames: Seq[DisplayLineNameAttr]): Option[NameDisplayLineRangeAttributeMap] = {
    if (lineNames.isEmpty) {
      return None
    }

    // group attributes by values, and get all Idx per group.
    val attrToSeqOfFeatureIdx: Array[LineNameAttrWithFeatures] = lineNames
      .sortBy(_.displayFeatureId)
      .zipWithIndex
      .groupMap { case (featureName, _) => featureName.nameAttrComponents } { case (_, idx) => idx }
      .map {
        case (nameComponents, featureIndices) => new LineNameAttrWithFeatures(nameComponents, featureIndices)
      }.toArray

    // create references from poi to attribute positions.
    // TODO: add sorting before index assignment as soon as determinism become a concern
    val featureIdxToAttrIdx: Array[Int] = attrToSeqOfFeatureIdx.zipWithIndex
      .flatMap { case (nameWithFeatures, nameIndex) =>
        nameWithFeatures.features.map(featureIndex => (featureIndex, nameIndex))
      }
      .sortBy(_._1)
      .map(_._2)

    val attrMap = new NameDisplayLineRangeAttributeMap(displayCoordinateShift)
    attrMap.setAttributeTypeCode(attributeType)

    attrMap.setFeature(lineNames.size)
    attrMap.setFeatureReferences(lineNames.map(lineName => toUndirectedLineRef(lineName.displayFeatureId)).toArray)
    attrMap.setFeatureValidities(fillValidity(lineNames.size))
    attrMap.setFeatureValuePtr(featureIdxToAttrIdx)
    attrMap.setAttribute(attrToSeqOfFeatureIdx.length)

    val featureNamesComponents = attrToSeqOfFeatureIdx.map(_.nameAttrComponents)
    attrMap.setAttributeValues(featureNamesComponents.map(_.nameAttribute))
    attrMap.setAttributeProperties(featureNamesComponents.map(_.properties))
    attrMap.setAttributeConditions(featureNamesComponents.map(_.conditions))

    Some(attrMap)
  }

  private def toUndirectedLineRef(lineId: Int): DisplayLineReference = {
    val lineRef = new DisplayLineReference()
    lineRef.setIsDirected(false)
    lineRef.setLineReference(new Var4ByteId(lineId))
    lineRef
  }

  private def fillValidity(size: Int): Array[DisplayLineRangeValidity] = Array.fill(size)(
    new DisplayLineRangeValidity(displayCoordinateShift, DisplayLineValidityType.COMPLETE, 0, Array.empty)
  )
}
