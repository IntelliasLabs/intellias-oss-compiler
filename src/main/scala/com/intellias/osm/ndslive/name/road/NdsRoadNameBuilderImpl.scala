package com.intellias.osm.ndslive.name.road

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.model.common.{DirectionType, Side}
import com.intellias.osm.model.name.Name
import com.intellias.osm.model.road.NdsRoad
import com.intellias.osm.ndslive.common.DirectionDetector
import com.intellias.osm.ndslive.name.{NameBuilder, NdsAdminTileBuilder, NdsNameEnvironment}
import nds.core.attributemap.ConditionList
import nds.name.attributes.{NameRoadRangeAttributeType, NameRoadRangeAttributeValue}
import nds.name.instantiations.{NamePropertyList, NameRoadRangeAttribute, NameRoadRangeAttributeMap}
import play.api.libs.json.Json

case class NdsRoadNameBuilderImpl(env: NdsNameEnvironment) extends NdsRoadNameBuilder with NameBuilder with DirectionDetector {
  override val attributeTypeCode: NameRoadRangeAttributeType = NameRoadRangeAttributeType.ROAD_NAME

  override def buildAttributes(roads: Array[NdsRoad], adminTileBuilder: NdsAdminTileBuilder): Option[NameRoadRangeAttributeMap] = {
    val roadToNames: Array[(NdsRoad, Seq[Name])] = roads.flatMap { road =>
      road.tags
        .get(OsmNameExtractor.tag)
        .map(str => Json.parse(str).as[Seq[Name]])
        .map(names => (road, names))
    }

    val seq: Seq[(RoadIdAndDirection, NameRoadRangeAttribute, NamePropertyList, ConditionList)] = roadToNames.flatMap {
      case (road, names) => buildRoadNameAttributes(road, names)
    }

    buildAttrMap(seq)
  }

  private def buildRoadNameAttributes(road: NdsRoad, names: Seq[Name]): Seq[(RoadIdAndDirection, NameRoadRangeAttribute, NamePropertyList, ConditionList)] = {
    names.map(name => buildOneRoadNameAttribute(road, name, names.size))
  }

  private def buildOneRoadNameAttribute(road: NdsRoad, name: Name, countOfNames: Int): (RoadIdAndDirection, NameRoadRangeAttribute, NamePropertyList, ConditionList) = {
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

    ( (road.ndsRoadId, sideToDirection(name.side)), buildNameAttribute(name), namePropertyList, emptyCondition)
  }

  private def buildNameAttribute(name: Name): NameRoadRangeAttribute = {
    val nameAttributeValue = new NameRoadRangeAttributeValue(attributeTypeCode)
    nameAttributeValue.setRoadName(name.name)

    val nameRoadAttribute = new NameRoadRangeAttribute(attributeTypeCode)
    nameRoadAttribute.setAttributeValue(nameAttributeValue)

    nameRoadAttribute
  }

}
