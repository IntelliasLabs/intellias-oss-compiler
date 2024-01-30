package com.intellias.osm.ndslive.name.display.point

import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.intellias.osm.ndslive.name.AttributeMapBuilder
import nds.core.types.Var4ByteId
import nds.name.attributes.NameDisplayPointAttributeType
import nds.name.instantiations.NameDisplayPointAttributeMap

trait NamePointAttributeMapBuilder extends AttributeMapBuilder {
  val attributeType: NameDisplayPointAttributeType

  protected def buildAttrMap(pointNames: Seq[DisplayPointNameAttr]): Option[NameDisplayPointAttributeMap] = {
    if (pointNames.isEmpty) {
      return None
    }

    // group attributes by values, and get all Idx per group.
    val attrToSeqOfFeatureIdx: Array[PointNameAttrWithFeatures] = pointNames
      .sortBy(_.displayFeatureId)
      .zipWithIndex
      .groupMap { case (featureName, _) => featureName.nameAttrComponents } { case (_, idx) => idx }
      .map {
        case (nameComponents, featureIndices) => new PointNameAttrWithFeatures(nameComponents, featureIndices)
      }.toArray

    // create references from poi to attribute positions.
    // TODO: add sorting before index assignment as soon as determinism become a concern
    val featureIdxToAttrIdx: Array[Int] = attrToSeqOfFeatureIdx.zipWithIndex
      .flatMap { case (nameWithFeatures, nameIndex) =>
        nameWithFeatures.features.map(featureIndex => (featureIndex, nameIndex))
      }
      .sortBy(_._1)
      .map(_._2)

    val attrMap = new NameDisplayPointAttributeMap(displayCoordinateShift)
    attrMap.setAttributeTypeCode(attributeType)

    attrMap.setFeature(pointNames.size)
    attrMap.setFeatureReferences(pointNames.map(pointName => new Var4ByteId(pointName.displayFeatureId)).toArray)
    attrMap.setFeatureValidities(fillValidities(pointNames.size))
    attrMap.setFeatureValuePtr(featureIdxToAttrIdx)
    attrMap.setAttribute(attrToSeqOfFeatureIdx.length)

    val featureNamesComponents = attrToSeqOfFeatureIdx.map(_.nameAttrComponents)
    attrMap.setAttributeValues(featureNamesComponents.map(_.nameAttribute))
    attrMap.setAttributeProperties(featureNamesComponents.map(_.properties))
    attrMap.setAttributeConditions(featureNamesComponents.map(_.conditions))

    Some(attrMap)
  }
}
