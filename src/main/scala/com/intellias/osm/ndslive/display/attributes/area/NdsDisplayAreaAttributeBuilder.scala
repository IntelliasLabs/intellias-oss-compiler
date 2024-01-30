package com.intellias.osm.ndslive.display.attributes.area

import com.intellias.osm.compiler.display.attribute.SrcDisplayAreaAttribute
import com.intellias.osm.model.display.DisplayArea
import com.intellias.osm.ndslive.display.displayCoordinateShift
import nds.core.attributemap.{Condition, ConditionList}
import nds.core.types.Var4ByteId
import nds.display.details.attributes.{DisplayAreaAttributeType, DisplayAreaAttributeValue}
import nds.display.details.instantiations._
import nds.display.reference.types.{DisplayAreaChoice, DisplayAreaValidity, DisplayAreaValidityType}

trait NdsDisplayAreaAttributeBuilder[T <: SrcDisplayAreaAttribute] extends Serializable {
  val attributeType: DisplayAreaAttributeType
  val sourceTag: String
  private val emptyProperties = new DisplayPropertyList(0.toShort, Array.empty[DisplayProperty])
  private val emptyConditions = new ConditionList(0.toShort, Array.empty[Condition])

  def buildAttributes(areas: Iterable[DisplayArea]): Iterable[DisplayAreaAttributeSetMap] = {
    areas
      .withFilter(filter)
      .flatMap { displayArea =>
        displayArea.tags.get(sourceTag)
          .map(parseFromJson)
          .getOrElse(Seq.empty)
          .map(attribute => (attribute, displayArea))
      }
      .groupBy(_._1)
      .map { case (sourceAttrValue, features) => (toFullAttribute(sourceAttrValue), features)}
      .collect {
        case (Some(fullAttribute), features) => buildAttributeSetMap(features.map(_._2).toArray, fullAttribute)
      }
  }

  protected def parseFromJson(json: String): Seq[T]

  protected def filter(displayArea: DisplayArea): Boolean = true

  protected def buildAttributeSetMap(features: Array[DisplayArea], attribute: DisplayAreaFullAttribute): DisplayAreaAttributeSetMap = {
    val displayAreaAttrSetMap = new DisplayAreaAttributeSetMap(displayCoordinateShift)
    displayAreaAttrSetMap.setFeature(features.length)
    displayAreaAttrSetMap.setReferences(features.map(feature => new Var4ByteId(feature.localId)))
    displayAreaAttrSetMap.setValidities(features.map(buildValidity))
    displayAreaAttrSetMap.setAttributeSet(new DisplayAreaAttributeSet(1, Array(attribute)))
    displayAreaAttrSetMap
  }

  protected def buildValidity(feature: DisplayArea): DisplayAreaValidity = {
    new DisplayAreaValidity(
      displayCoordinateShift, DisplayAreaValidityType.COMPLETE, 0, Array.empty[DisplayAreaChoice]
    )
  }

  protected def toNdsAttribute(sourceAttrValue: T): Option[DisplayAreaAttributeValue]

  protected def toFullAttribute(sourceAttrValue: T): Option[DisplayAreaFullAttribute] = {
    toNdsAttribute(sourceAttrValue).map { ndsAttrValue =>
      new DisplayAreaFullAttribute(ndsAttrValue.getType, ndsAttrValue, emptyProperties, emptyConditions)
    }
  }
}
