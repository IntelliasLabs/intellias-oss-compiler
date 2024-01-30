package com.intellias.osm.ndslive.name

import com.intellias.osm.compiler.attributes.OsmNameExtractor
import com.intellias.osm.model.admin.{AdminTile, AdminTileItem, AdminType}
import com.intellias.osm.ndslive.name.NdsAdminTileBuilder.toAdministrativeHierarchyElement
import nds.name.types.{AdminHierarchyElementType, AdministrativeHierarchy, AdministrativeHierarchyElement}

class NdsAdminTileBuilder(adminMap: Map[String, Seq[AdminTileItem]], adminPlaces: Seq[AdminTileItem]) extends Serializable {

  def getAdminRefs(adminPlaceIds: String): AdministrativeHierarchy = {
    // ** List of administrative hierarchy elements, ordered from lowest to highest. http://nds.to/60pfq
    val adminRefsIds: Seq[Int] = adminMap(adminPlaceIds)
      .map(ap => ap.ndsId.toInt)

    new AdministrativeHierarchy(adminRefsIds.size, adminRefsIds.toArray)
  }

  def getAdminHierarchy: Array[AdministrativeHierarchyElement] = adminPlaces.map(toAdministrativeHierarchyElement).toArray
}

object NdsAdminTileBuilder {

  def apply(adminTile: AdminTile): NdsAdminTileBuilder = {
    val adminPlaces = adminTile.adminPlaces.map(api => api.adminPlace.adminPlaceId -> api).toMap

    val adminMap = adminTile.adminPlaces.foldLeft(Map.empty[String, Seq[AdminTileItem]]) {
      case (acc, api) =>
        val refs = api +: api.adminPlace.parents.map { apRef =>
          adminPlaces(apRef.adminPlaceId)
        }
        acc + (api.adminPlace.adminPlaceId -> refs)
    }

    new NdsAdminTileBuilder(adminMap, adminTile.adminPlaces)
  }

  def toAdministrativeHierarchyElement(adminTileItem: AdminTileItem): AdministrativeHierarchyElement = {
    val mainName = OsmNameExtractor.getMainName(adminTileItem.adminPlace.tags)

    new AdministrativeHierarchyElement(
      adminTileItem.ndsId.toInt,
      toAdminHierarchyElementType(adminTileItem.adminPlace.adminType.adminType),
      mainName.getOrElse("")
    )
  }

  private def toAdminHierarchyElementType(adminType: AdminType): AdminHierarchyElementType = adminType match {
    case AdminType.Country                 => AdminHierarchyElementType.COUNTRY
    case AdminType.SubCountrySet           => AdminHierarchyElementType.SUB_COUNTRY_SET
    case AdminType.SubCountry              => AdminHierarchyElementType.SUB_COUNTRY
    case AdminType.County                  => AdminHierarchyElementType.COUNTY
    case AdminType.Municipality            => AdminHierarchyElementType.MUNICIPALITY
    case AdminType.MunicipalitySubdivision => AdminHierarchyElementType.MUNICIPALITY_SUBDIVISION
    case AdminType.Hamlet                  => AdminHierarchyElementType.HAMLET
    case AdminType.Neighborhood            => AdminHierarchyElementType.NEIGHBORHOOD
    case AdminType.CityBlock               => AdminHierarchyElementType.CITY_BLOCK
  }
}
