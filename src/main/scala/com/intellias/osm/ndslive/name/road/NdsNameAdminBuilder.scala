package com.intellias.osm.ndslive.name.road

import com.intellias.osm.model.common.DirectionType
import com.intellias.osm.model.road.NdsRoad
import com.intellias.osm.ndslive.name.NdsAdminTileBuilder
import nds.core.attributemap.ConditionList
import nds.name.attributes.{NameRoadRangeAttributeType, NameRoadRangeAttributeValue}
import nds.name.instantiations.{NameProperty, NamePropertyList, NameRoadRangeAttribute, NameRoadRangeAttributeMap}
import nds.name.types.AdministrativeHierarchy

case object NdsNameAdminBuilder extends NdsRoadNameBuilder {
  override val attributeTypeCode: NameRoadRangeAttributeType = NameRoadRangeAttributeType.ADMINISTRATIVE_HIERARCHY

  override def buildAttributes(roads: Array[NdsRoad], adminTileBuilder: NdsAdminTileBuilder): Option[NameRoadRangeAttributeMap] = {
    buildAttrMap {
      val adminRefs = roads.flatMap { road =>
        Seq(road.leftAdmin, road.rightAdmin).flatten.distinct.map { ap =>
          buildNameAdminRefAttributes(road.ndsRoadId, adminTileBuilder.getAdminRefs(ap.adminPlaceId))
        }
      }.distinct
      adminRefs
    }
  }

  private def buildNameAdminRefAttributes(
      featureId: Int,
      adminHierarchy: AdministrativeHierarchy): (RoadIdAndDirection, NameRoadRangeAttribute, NamePropertyList, ConditionList) = {
    val nameAttributeValue = new NameRoadRangeAttributeValue(attributeTypeCode)
    nameAttributeValue.setAdministrativeHierarchy(adminHierarchy)

    val nameRoadAttribute = new NameRoadRangeAttribute(attributeTypeCode)
    nameRoadAttribute.setAttributeValue(nameAttributeValue)

    ((featureId, DirectionType.Both), nameRoadAttribute, new NamePropertyList(0.toShort, Array.empty[NameProperty]), emptyCondition)
  }
}
