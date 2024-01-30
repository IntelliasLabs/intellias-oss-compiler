package com.intellias.osm.ndslive.name.display.area

import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.intellias.osm.ndslive.name.AttributeMapBuilder
import nds.core.types.Var4ByteId
import nds.display.reference.types.{DisplayAreaValidity, DisplayAreaValidityType}
import nds.name.attributes.NameDisplayAreaAttributeType
import nds.name.instantiations.NameDisplayAreaAttributeMap

trait NameAreaAttributeMapBuilder extends AttributeMapBuilder {
  val attributeType: NameDisplayAreaAttributeType

  def buildAttrMap(areaNames: Seq[DisplayAreaNameAttr]): Option[NameDisplayAreaAttributeMap] = {
    if (areaNames.isEmpty) {
      return None
    }

    // group attributes by values, and get all Idx per group.
    val attrToSeqOfFeatureIdx: Array[AreaNameAttrWithFeatures] = areaNames
      .sortBy(_.displayFeatureId)
      .zipWithIndex
      .groupMap { case (featureName, _) => featureName.nameAttrComponents } { case (_, idx) => idx }
      .map { case (nameComponents, featureIndices) =>
        new AreaNameAttrWithFeatures(nameComponents, featureIndices)
      }.toArray

    // create references from poi to attribute positions.
    // TODO: add sorting before index assignment as soon as determinism become a concern
    val featureIdxToAttrIdx: Array[Int] = attrToSeqOfFeatureIdx.zipWithIndex
      .flatMap { case (nameWithFeatures, nameIndex) =>
        nameWithFeatures.features.map(featureIndex => (featureIndex, nameIndex))
      }
      .sortBy(_._1)
      .map(_._2)

    val attrMap = new NameDisplayAreaAttributeMap(displayCoordinateShift)
    attrMap.setAttributeTypeCode(attributeType)

    attrMap.setFeature(areaNames.size)
    attrMap.setFeatureReferences(areaNames.map(areaName => new Var4ByteId(areaName.displayFeatureId)).toArray)
    attrMap.setFeatureValidities(fillValidity(areaNames.size))
    attrMap.setFeatureValuePtr(featureIdxToAttrIdx)
    attrMap.setAttribute(attrToSeqOfFeatureIdx.length)

    val featureNamesComponents = attrToSeqOfFeatureIdx.map(_.nameAttrComponents)
    attrMap.setAttributeValues(featureNamesComponents.map(_.nameAttribute))
    attrMap.setAttributeProperties(featureNamesComponents.map(_.properties))
    attrMap.setAttributeConditions(featureNamesComponents.map(_.conditions))

    Some(attrMap)
  }

  private def fillValidity(size: Int): Array[DisplayAreaValidity] = Array.fill(size)(
    new DisplayAreaValidity(displayCoordinateShift, DisplayAreaValidityType.COMPLETE, 0, Array.empty)
  )
}
