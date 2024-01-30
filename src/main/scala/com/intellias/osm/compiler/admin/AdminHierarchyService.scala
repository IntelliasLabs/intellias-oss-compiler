package com.intellias.osm.compiler.admin

import com.intellias.osm.model.admin.{AdminType, AdminTypeWrapper}
import com.intellias.osm.{AdminHierarchyConfig, AppConfig}
import pureconfig.generic.auto._


trait AdminHierarchyService {
  val adminHierarchyService: AdminHierarchyService.Service
}

object AdminHierarchyService {
  trait Service extends Serializable {
    def resolveAdminType(isoCountryCode: String, level: Int): AdminType
  }

  def apply(adminPlaceConfig: AdminHierarchyConfig): AdminHierarchyService = new AdminHierarchyService {
    override val adminHierarchyService: AdminHierarchyService.Service = AdminHierarchyServiceImpl(adminPlaceConfig)
  }
}


class AdminHierarchyServiceImpl(val countriesHierarchy: List[CountryHierarchy])
  extends AdminHierarchyService.Service {

  override def resolveAdminType(isoCountryCode: String, level: Int): AdminType = {
    countriesHierarchy.find(c => c.isoCountryCode == isoCountryCode)
      .flatMap(h => h.hierarchyMapping.find(he => he.level == level).map(_.hierarchyType))
      .getOrElse(throw new IllegalStateException(s"Not found hierarchy type for $isoCountryCode level: $level"))
  }
}

object AdminHierarchyServiceImpl {
  def apply(adminPlaceConfig: AdminHierarchyConfig): AdminHierarchyServiceImpl = {
    new AdminHierarchyServiceImpl(readHierarchyConf(adminPlaceConfig))
  }

  private def readHierarchyConf(adminPlaceConfig: AdminHierarchyConfig): List[CountryHierarchy] = {
    val countriesHierarchy = AppConfig.read[CountriesHierarchyMapping](Some(adminPlaceConfig.countriesHierarchyPath)).countries

    countriesHierarchy.map { country =>
      CountryHierarchy(
        country.isoCountryCode,
        country.hierarchyMapping.map(el => CountryHierarchyElement(el.level, el.name, toAdminType(el.hierarchyType)))
      )
    }
  }

  private def toAdminType(typeStr: String): AdminType = AdminTypeWrapper.toAdminType(typeStr)
}