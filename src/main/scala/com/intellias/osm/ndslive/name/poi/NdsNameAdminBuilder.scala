package com.intellias.osm.ndslive.name.poi

import com.intellias.osm.model.poi.POI
import com.intellias.osm.ndslive.name.NdsAdminTileBuilder
import nds.core.attributemap.ConditionList
import nds.name.attributes.{NamePoiAttributeType, NamePoiAttributeValue}
import nds.name.instantiations.{NamePoiAttribute, NamePoiAttributeMap, NameProperty, NamePropertyList}
import nds.name.types.AdministrativeHierarchy

case object NdsNameAdminBuilder extends NdsPoiNameBuilder {
  override val attributeTypeCode: NamePoiAttributeType = NamePoiAttributeType.ADMINISTRATIVE_HIERARCHY

  override def buildAttributes(pois: Array[POI], adminTileBuilder: NdsAdminTileBuilder): Option[NamePoiAttributeMap] = {
    buildAttrMap{
      pois.flatMap { poi =>
        poi.adminPlace.map { ap =>
          buildNameAdminRefAttributes(poi.ndsId, adminTileBuilder.getAdminRefs(ap.adminPlaceId))
        }
      }
    }
  }

  private def buildNameAdminRefAttributes(featureId: Int,
                                          adminHierarchy: AdministrativeHierarchy): (PoiId, NamePoiAttribute, NamePropertyList, ConditionList) = {
    val nameAttributeValue = new NamePoiAttributeValue(attributeTypeCode)
    nameAttributeValue.setAdministrativeHierarchy(adminHierarchy)

    val namePoiAttribute = new NamePoiAttribute(attributeTypeCode)
    namePoiAttribute.setAttributeValue(nameAttributeValue)

    (featureId, namePoiAttribute, new NamePropertyList(0.toShort, Array.empty[NameProperty]), emptyCondition)
  }
}
