package com.intellias.osm.ndslive.name

import com.intellias.osm.compiler.attributes.keys.OsmNameType
import com.intellias.osm.model.name.{Name, NameType}
import nds.core.types.Flag
import nds.name.instantiations.NameProperty
import nds.name.properties.{NamePropertyType, NamePropertyValue, PropertyType, PropertyValue}
import nds.name.types.{NameDetailType, NameUsageType}

trait NameBuilder extends Serializable {


  def buildNameDetailTypeProperty(name: Name): Option[NameProperty] = {
    val detailType = name.nameType match {
      case NameType.Name if !name.isOfficial => Some(NameDetailType.EXONYM)
      case NameType.ShortName => Some(NameDetailType.SYNONYM)
      case NameType.Alternate => Some(NameDetailType.ALTERNATE_SPELLING)
      case _ => None
    }

    detailType.map { detailType =>
      val namePropertyType = new NamePropertyType(PropertyType.DETAIL_TYPE, null)
      val namePropertyValue = new PropertyValue(PropertyType.DETAIL_TYPE)
      namePropertyValue.setDetailType(detailType)

      new NameProperty(
        namePropertyType,
        new NamePropertyValue(namePropertyType, namePropertyValue, null)
      )
    }
  }


  def buildNameLangProperty(langId: Short): Option[NameProperty] = {
    val namePropertyType = new NamePropertyType(PropertyType.LANGUAGE_CODE, null)
    val namePropertyValue = new PropertyValue(PropertyType.LANGUAGE_CODE)
    namePropertyValue.setLanguageCode(langId)

    Some(
      new NameProperty(
        namePropertyType,
        new NamePropertyValue(namePropertyType, namePropertyValue, null)
      ))
  }

  def buildNameUsageTypeProperty(name: Name): Option[NameProperty] = {
    val namePropertyType = new NamePropertyType(PropertyType.USAGE_TYPE, null)
    val namePropertyValue = new PropertyValue(PropertyType.USAGE_TYPE)
    namePropertyValue.setUsageType(toUsageType(name))

    Some(
      new NameProperty(
        namePropertyType,
        new NamePropertyValue(namePropertyType, namePropertyValue, null)
      ))
  }

  def toUsageType(name: Name): NameUsageType = name match {
    case name if name.isDefault => NameUsageType.DEFAULT_OFFICIAL_NAME
    case name if name.isOfficial || name.nameType == NameType.OfficialName => NameUsageType.OFFICIAL_NAME
    case _ => NameUsageType.ALTERNATE_NAME
  }

  def isPreferredProperty(name: Name, countOfNames: Int): Option[NameProperty] = {
    if (name.isDefault && countOfNames > 1) {
      val namePropertyType = new NamePropertyType(PropertyType.PREFERRED_NAME, null)
      val namePropertyValue = new PropertyValue(PropertyType.PREFERRED_NAME)
      namePropertyValue.setPreferredName(new Flag())

      Some(
        new NameProperty(
          namePropertyType,
          new NamePropertyValue(namePropertyType, namePropertyValue, null)
        )
      )
    } else None
  }

}
