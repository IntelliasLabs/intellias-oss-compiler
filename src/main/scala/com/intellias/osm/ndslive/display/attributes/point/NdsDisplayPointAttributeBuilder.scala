package com.intellias.osm.ndslive.display.attributes.point

import com.intellias.osm.compiler.display.attribute.SrcDisplayPointAttribute
import com.intellias.osm.compiler.display.attribute.value.point.Population
import com.intellias.osm.model.display.DisplayPoint
import com.intellias.osm.ndslive.display.displayCoordinateShift
import com.intellias.osm.tools.JsonMapperProvider
import nds.core.attributemap.{Condition, ConditionList, Validity}
import nds.core.types.Var4ByteId
import nds.display.details.attributes.DisplayPointAttributeType
import nds.display.details.instantiations.{DisplayPointAttributeSet, DisplayPointAttributeSetMap, DisplayPointFullAttribute, DisplayProperty, DisplayPropertyList}

trait NdsDisplayPointAttributeBuilder[A <: SrcDisplayPointAttribute] extends JsonMapperProvider {
  val sourceTag: String
  val attributeType: DisplayPointAttributeType

  protected val emptyProperties = new DisplayPropertyList(0.toShort, Array.empty[DisplayProperty])
  protected val emptyConditions = new ConditionList(0.toShort, Array.empty[Condition])
  protected val fullValidity = new Validity(displayCoordinateShift)

  def buildAttributes(points: Iterable[DisplayPoint]): Iterable[DisplayPointAttributeSetMap] = {
    points
      .withFilter(filter)
      .flatMap { displayPoint =>
        displayPoint.tags.get(sourceTag)
          .map(parseSourceAttribute)
          .getOrElse(Seq.empty)
          .map(sourceAttrValue => (sourceAttrValue, displayPoint))
      }
      .groupBy(_._1)
      .map { case (sourceAttrValue, features) =>
        buildAttributeSetMap(features.map(_._2).toArray, toFullAttribute(sourceAttrValue))
      }
  }

  def buildAttributeSetMap(features: Array[DisplayPoint], attribute: DisplayPointFullAttribute): DisplayPointAttributeSetMap = {
    val displayAreaAttrSetMap = new DisplayPointAttributeSetMap(displayCoordinateShift)
    displayAreaAttrSetMap.setFeature(features.length)
    displayAreaAttrSetMap.setReferences(features.map(feature => new Var4ByteId(feature.localId)))
    displayAreaAttrSetMap.setValidities(features.map(computeValidity))
    displayAreaAttrSetMap.setAttributeSet(new DisplayPointAttributeSet(1, Array(attribute)))
    displayAreaAttrSetMap
  }

  protected def parseSourceAttribute(sourceAttrJson: String): Seq[A]

  protected def toFullAttribute(srcAttributeValue: A): DisplayPointFullAttribute

  protected def filter(point: DisplayPoint): Boolean = true

  protected def computeValidity(point: DisplayPoint): Validity = fullValidity
}
