package com.intellias.osm.ndslive.name.display.point

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.model.display.DisplayPoint
import com.intellias.osm.model.name.Name
import com.intellias.osm.ndslive.name.NameBuilder
import nds.name.attributes.NameDisplayPointAttributeValue
import nds.name.instantiations.{NameDisplayPointAttribute, NameDisplayPointAttributeMap, NamePropertyList}
import play.api.libs.json.Json

trait NdsDisplayPointNameBuilder extends NamePointAttributeMapBuilder with NameBuilder with Serializable {
  protected val pointFilter: DisplayPoint => Boolean

  protected def setNameAttrValue(name: Name, attrValue: NameDisplayPointAttributeValue): Unit

  def buildAttributes(points: Seq[DisplayPoint]): Option[NameDisplayPointAttributeMap] = {
    val pointToNames = points
      .withFilter(pointFilter)
      .flatMap { point =>
        point.tags
          .get(OsmNameExtractor.tag)
          .map(str => Json.parse(str).as[Seq[Name]])
          .map(names => (point, names))
      }

    val displayFeatureNames: Seq[DisplayPointNameAttr] = pointToNames.flatMap {
      case (point, names) => buildPointNameAttributes(point, names)
    }

    buildAttrMap(displayFeatureNames)
  }

  private def buildPointNameAttributes(point: DisplayPoint, names: Seq[Name]): Seq[DisplayPointNameAttr] = {
    names.map(name => buildOnePointNameAttribute(point, name, names.size))
  }

  private def buildOnePointNameAttribute(point: DisplayPoint, name: Name, countOfNames: Int): DisplayPointNameAttr = {
    val namePropertyList: NamePropertyList = new NamePropertyList()
    namePropertyList.setProperty {
      Array(
        buildNameLangProperty(name.langId),
        buildNameUsageTypeProperty(name),
        buildNameDetailTypeProperty(name),
        isPreferredProperty(name, countOfNames)
      ).flatten
    }
    namePropertyList.setNumProperties(namePropertyList.getProperty.length.toShort)

    new DisplayPointNameAttr(
      point.localId,
      new PointNameAttrComponents(buildNameAttribute(name), namePropertyList, emptyCondition)
    )
  }

  private def buildNameAttribute(name: Name): NameDisplayPointAttribute = {
    val nameAttributeValue = new NameDisplayPointAttributeValue(attributeType)
    setNameAttrValue(name, nameAttributeValue)
    new NameDisplayPointAttribute(attributeType, nameAttributeValue)
  }
}