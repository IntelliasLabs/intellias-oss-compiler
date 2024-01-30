package com.intellias.osm.ndslive.name.display.area

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.model.display.DisplayArea
import com.intellias.osm.model.name.Name
import com.intellias.osm.ndslive.name.NameBuilder
import nds.name.attributes.NameDisplayAreaAttributeValue
import nds.name.instantiations.{NameDisplayAreaAttribute, NameDisplayAreaAttributeMap, NamePropertyList}
import play.api.libs.json.Json

trait NdsDisplayAreaNameBuilder extends NameBuilder with NameAreaAttributeMapBuilder with Serializable {
  protected val areaFilter: DisplayArea => Boolean
  protected def setNameAttrValue(name: Name, attrValue: NameDisplayAreaAttributeValue): Unit

  def buildAttributes(areas: Seq[DisplayArea]): Option[NameDisplayAreaAttributeMap] = {
    val areaToNames = areas
      .withFilter(areaFilter)
      .flatMap { area =>
        area.tags
          .get(OsmNameExtractor.tag)
          .map(str => Json.parse(str).as[Seq[Name]])
          .map(names => (area, names))
      }

    val displayFeatureNames: Seq[DisplayAreaNameAttr] = areaToNames.flatMap {
      case (area, names) => buildAreaNameAttributes(area, names)
    }

    buildAttrMap(displayFeatureNames)
  }

  private def buildAreaNameAttributes(area: DisplayArea, names: Seq[Name]): Seq[DisplayAreaNameAttr] = {
    names.map(name => buildOneAreaNameAttribute(area, name, names.size))
  }

  private def buildOneAreaNameAttribute(area: DisplayArea, name: Name, countOfNames: Int): DisplayAreaNameAttr = {
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

    new DisplayAreaNameAttr(
      area.localId,
      new AreaNameAttrComponents(buildNameAttribute(name), namePropertyList, emptyCondition)
    )
  }

  private def buildNameAttribute(name: Name): NameDisplayAreaAttribute = {
    val nameAttributeValue = new NameDisplayAreaAttributeValue(attributeType)
    setNameAttrValue(name, nameAttributeValue)
    new NameDisplayAreaAttribute(attributeType, nameAttributeValue)
  }
}
