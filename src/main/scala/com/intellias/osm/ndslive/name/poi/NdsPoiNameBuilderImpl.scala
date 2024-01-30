package com.intellias.osm.ndslive.name.poi

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.model.name.Name
import com.intellias.osm.model.poi.POI
import com.intellias.osm.ndslive.name.{NameBuilder, NdsAdminTileBuilder, NdsNameEnvironment}
import nds.core.attributemap.ConditionList
import nds.core.types.Flag
import nds.name.attributes.{NamePoiAttributeType, NamePoiAttributeValue}
import nds.name.instantiations.{NamePoiAttribute, NamePoiAttributeMap, NameProperty, NamePropertyList}
import nds.name.properties.{NamePropertyType, NamePropertyValue, PropertyType, PropertyValue}
import play.api.libs.json.Json

case class NdsPoiNameBuilderImpl(env: NdsNameEnvironment) extends NdsPoiNameBuilder with NameBuilder {
  override val attributeTypeCode: NamePoiAttributeType = NamePoiAttributeType.NAME

  override def buildAttributes(pois: Array[POI], adminTileBuilder: NdsAdminTileBuilder): Option[NamePoiAttributeMap] = {
    val poiToNames: Array[(POI, Seq[Name])] = pois.flatMap { poi =>
      poi.tags
        .get(OsmNameExtractor.tag)
        .map(str => Json.parse(str).as[Seq[Name]])
        .map(names => (poi, names))
    }

    val seq: Seq[(PoiId, NamePoiAttribute, NamePropertyList, ConditionList)] = poiToNames.flatMap {
      case (poi, names) => buildPoiNameAttributes(poi, names)
    }

    buildAttrMap(seq)
  }

  private def buildPoiNameAttributes(poi: POI, names: Seq[Name]): Seq[(PoiId, NamePoiAttribute, NamePropertyList, ConditionList)] = {
    names.map(name => buildOnePoiNameAttribute(poi, name, names.size))
  }

  private def buildOnePoiNameAttribute(poi: POI, name: Name, countOfNames: Int): (PoiId, NamePoiAttribute, NamePropertyList, ConditionList) = {
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

    (poi.ndsId, buildNameAttribute(name), namePropertyList, emptyCondition)
  }

  private def buildNameAttribute(name: Name): NamePoiAttribute = {
    val nameAttributeValue = new NamePoiAttributeValue(attributeTypeCode)
    nameAttributeValue.setPoiName(name.name)

    val namePoiAttribute = new NamePoiAttribute(attributeTypeCode)
    namePoiAttribute.setAttributeValue(nameAttributeValue)

    namePoiAttribute
  }

}
