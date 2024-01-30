package com.intellias.osm.compiler.attributes.keys

import com.intellias.osm.model.name.NameType

abstract class OsmNameType(val values: String, val nameType: NameType) {
  override def toString: String = values
}

object OsmNameType extends OsmCompositeKey[OsmNameType] {
  override val values: Seq[OsmNameType] =  Seq(
    Name,
    OfficialName,
    InternationalName,
    LocalName,
    OldName,
    RegionalName,
    ShortName,
    Alternate,
    NickName,
    SortingName
  )

  override def groupName: String = "name"
  override def default: OsmNameType = Name

  case object Name extends OsmNameType("name", NameType.Name)
  case object OfficialName extends OsmNameType("official_name", NameType.OfficialName)
  case object InternationalName extends OsmNameType("", NameType.InternationalName)
  case object LocalName extends OsmNameType("loc_name", NameType.LocalName)
  case object OldName extends OsmNameType("old_name", NameType.OldName)
  case object RegionalName extends OsmNameType("reg_name", NameType.RegionalName)
  case object ShortName extends OsmNameType("short_name", NameType.ShortName)
  case object SortingName extends OsmNameType("sorting_name", NameType.SortingName)
  case object Alternate extends OsmNameType("alt_name", NameType.Alternate)
  case object NickName extends OsmNameType("nickname", NameType.NickName)
}