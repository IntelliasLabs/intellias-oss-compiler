package com.intellias.osm.compiler

import com.intellias.osm.common.SourceType
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.model.admin.{AdminPlace, AdminPlaceGround, AdminTile, AdminType}
import play.api.libs.json.{Format, Json}

package object admin {
  trait AdminEnvironment extends LanguageService with AdminHierarchyService with Serializable

  case class DisputedArea(disputeId: Long, isoCountryCode: String, regionCode: String)
  implicit val disputedAresFormat: Format[DisputedArea] = Json.format[DisputedArea]


  case class DisputedView(isoCountryCode: String)

  implicit val disputedViewFormat: Format[DisputedView] = Json.format[DisputedView]




  case class CountryHierarchyElementConf(level: Int, name: String, hierarchyType: String)
  case class CountryHierarchyConf(isoCountryCode: String, hierarchyMapping: List[CountryHierarchyElementConf])
  case class CountriesHierarchyMapping(countries: List[CountryHierarchyConf])

  case class CountryHierarchyElement(level: Int, name: String, hierarchyType: AdminType)
  case class CountryHierarchy(isoCountryCode: String, hierarchyMapping: List[CountryHierarchyElement])

  case object AdminHierarchyTable extends SourceType[AdminPlace]
  case object AdminPlaceGroundTable extends SourceType[AdminPlaceGround]
  case object AdminTileTable extends SourceType[AdminTile]

}
