package com.intellias.osm.ndslive.name.road

import com.intellias.osm.compiler.road.characteristic.range.RoadNumberExtractor
import com.intellias.osm.compiler.schema.model.road.RoadNumber
import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.NdsRoad
import com.intellias.osm.ndslive.name.road.NdsRoadNumberBuilder.{PrefixNumberSuffix, PrefixSuffixPattern, numberGroup, prefGroup, suffixGroup, toPrefixNumberSuffix}
import com.intellias.osm.ndslive.name.{NameBuilder, NdsAdminTileBuilder, NdsNameEnvironment}
import nds.core.attributemap.ConditionList
import nds.core.types.UNDEFINED_LANGUAGE_CODE
import nds.name.attributes.{NameRoadRangeAttributeType, NameRoadRangeAttributeValue}
import nds.name.instantiations.{NameProperty, NamePropertyList, NameRoadRangeAttribute, NameRoadRangeAttributeMap}
import nds.name.properties.{NamePropertyType, NamePropertyValue, PropertyType, PropertyValue}
import nds.name.types.RoadNumberComponent
import play.api.libs.json.Json

import scala.util.matching.Regex

case class NdsRoadNumberBuilder(env: NdsNameEnvironment) extends NdsRoadNameBuilder with NameBuilder {
  override val attributeTypeCode: NameRoadRangeAttributeType = NameRoadRangeAttributeType.ROAD_NUMBER



  override def buildAttributes(roads: Array[NdsRoad], adminTileBuilder: NdsAdminTileBuilder): Option[NameRoadRangeAttributeMap] = {
    val roadToNumber: Array[(NdsRoad, Seq[RoadNumber])] = roads.flatMap { road =>
      road.tags
        .get(RoadNumberExtractor.tag)
        .map(str => Json.parse(str).as[Seq[RoadNumber]])
        .map(roadNumbers => (road, roadNumbers))
    }

    val seq: Seq[(RoadIdAndDirection, NameRoadRangeAttribute, NamePropertyList, ConditionList)] = roadToNumber.flatMap {
      case (road, roadNumbers) => buildRoadNumberAttributes(road, roadNumbers)
    }

    buildAttrMap(seq)
  }

  private def buildRoadNumberAttributes(road: NdsRoad,
                                        roadNumbers: Seq[RoadNumber]): Seq[(RoadIdAndDirection, NameRoadRangeAttribute, NamePropertyList, ConditionList)] = {
    roadNumbers.map(name => buildOneRoadNameAttribute(road, name))
  }

  private def buildOneRoadNameAttribute(road: NdsRoad,
                                        roadNumber: RoadNumber
                                       ): (RoadIdAndDirection, NameRoadRangeAttribute, NamePropertyList, ConditionList) = {
    val prefNumSuf                         = toPrefixNumberSuffix(roadNumber)
    val namePropertyList: NamePropertyList = new NamePropertyList()

    namePropertyList.setProperty {
      Array(
        buildNameLangProperty(UNDEFINED_LANGUAGE_CODE.UNDEFINED_LANGUAGE_CODE),
        prefNumSuf.prefix.map(prefix => toProperty(prefix, PropertyType.ROAD_NUMBER_PREFIX)),
        prefNumSuf.suffix.map(suffix => toProperty(suffix, PropertyType.ROAD_NUMBER_SUFFIX))
      ).flatten
    }

    namePropertyList.setNumProperties(namePropertyList.getProperty.length.toShort)

    ((road.ndsRoadId, DirectionType.Both), buildNumberAttribute(prefNumSuf.number), namePropertyList, emptyCondition)
  }

  private def buildNumberAttribute(number: String): NameRoadRangeAttribute = {
    val nameAttributeValue = new NameRoadRangeAttributeValue(attributeTypeCode)
    nameAttributeValue.setRoadNumber(number)

    val numberRoadAttribute = new NameRoadRangeAttribute(attributeTypeCode)
    numberRoadAttribute.setAttributeValue(nameAttributeValue)

    numberRoadAttribute
  }



  private def toProperty(str: String, propType: PropertyType): NameProperty = {
    val namePropertyType  = new NamePropertyType(propType, null)
    val namePropertyValue = new PropertyValue(propType)

    namePropertyValue.setRoadNumberPrefix(new RoadNumberComponent(str, true))

    new NameProperty(
      namePropertyType,
      new NamePropertyValue(namePropertyType, namePropertyValue, null)
    )
  }

}

object NdsRoadNumberBuilder {
  private val prefGroup                  = "prefix"
  private val numberGroup                = "number"
  private val suffixGroup                = "suffix"
  private val PrefixSuffixPattern: Regex = s"(?<$prefGroup>[a-zA-Z\\s]+)?(?<$numberGroup>\\d+)(?<$suffixGroup>[a-zA-Z()\\s]+)?".r

  case class PrefixNumberSuffix(
      number: String,
      prefix: Option[String] = None,
      suffix: Option[String] = None
  )

  def toPrefixNumberSuffix(roadNumber: RoadNumber): PrefixNumberSuffix = {
    PrefixSuffixPattern
      .findFirstMatchIn(roadNumber.number)
      .map { m: Regex.Match =>
        PrefixNumberSuffix(Option(m.group(numberGroup)).getOrElse(""), Option(m.group(prefGroup)), Option(m.group(suffixGroup)))
      }
      .getOrElse(PrefixNumberSuffix(""))
  }
}
