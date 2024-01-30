package com.intellias.osm.ndslive.name.display.line

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.model.display.DisplayLine
import com.intellias.osm.model.name.Name
import com.intellias.osm.ndslive.name.NameBuilder
import nds.name.attributes.NameDisplayLineRangeAttributeValue
import nds.name.instantiations.{NameDisplayLineRangeAttribute, NameDisplayLineRangeAttributeMap, NamePropertyList}
import play.api.libs.json.Json

trait NdsDisplayLineNameBuilder extends NameLineAttributeMapBuilder with NameBuilder with Serializable {
  protected val lineFilter: DisplayLine => Boolean

  protected def setNameAttrValue(name: Name, attrValue: NameDisplayLineRangeAttributeValue): Unit

  def buildAttributes(lines: Seq[DisplayLine]): Option[NameDisplayLineRangeAttributeMap] = {
    val lineToNames = lines
      .withFilter(lineFilter)
      .flatMap { line =>
        line.tags
          .get(OsmNameExtractor.tag)
          .map(str => Json.parse(str).as[Seq[Name]])
          .map(names => (line, names))
      }

    val displayFeatureNames: Seq[DisplayLineNameAttr] = lineToNames.flatMap {
      case (line, names) => buildLineNameAttributes(line, names)
    }

    buildAttrMap(displayFeatureNames)
  }

  private def buildLineNameAttributes(line: DisplayLine, names: Seq[Name]): Seq[DisplayLineNameAttr] = {
    names.map(name => buildOneLineNameAttribute(line, name, names.size))
  }

  private def buildOneLineNameAttribute(line: DisplayLine, name: Name, countOfNames: Int): DisplayLineNameAttr = {
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

    new DisplayLineNameAttr(
      line.localId,
      new LineNameAttrComponents(buildNameAttribute(name), namePropertyList, emptyCondition)
    )
  }

  private def buildNameAttribute(name: Name): NameDisplayLineRangeAttribute = {
    val nameAttributeValue = new NameDisplayLineRangeAttributeValue(attributeType)
    setNameAttrValue(name, nameAttributeValue)
    new NameDisplayLineRangeAttribute(attributeType, nameAttributeValue)
  }
}
