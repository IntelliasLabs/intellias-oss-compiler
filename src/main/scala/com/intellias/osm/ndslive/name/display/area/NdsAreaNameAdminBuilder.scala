package com.intellias.osm.ndslive.name.display.area
import com.intellias.osm.model.display.DisplayArea
import com.intellias.osm.ndslive.name.NdsAdminTileBuilder
import nds.name.attributes.{NameDisplayAreaAttributeType, NameDisplayAreaAttributeValue}
import nds.name.instantiations.{NameDisplayAreaAttribute, NameDisplayAreaAttributeMap, NameProperty, NamePropertyList}
import nds.name.types.AdministrativeHierarchy

object NdsAreaNameAdminBuilder extends NameAreaAttributeMapBuilder {
  override val attributeType: NameDisplayAreaAttributeType = NameDisplayAreaAttributeType.ADMINISTRATIVE_HIERARCHY

  def buildAttributes(areas: Seq[DisplayArea], adminTileBuilder: NdsAdminTileBuilder): Option[NameDisplayAreaAttributeMap] = {
    val adminHierarchyRefs = areas.flatMap { area =>
      area.adminPlaces.map(ap => buildAdminHierarchyRefAttribute(area.localId, adminTileBuilder.getAdminRefs(ap.adminPlaceId)))
    }
    buildAttrMap(adminHierarchyRefs)
  }

  private def buildAdminHierarchyRefAttribute(areaId: Int, adminHierarchy: AdministrativeHierarchy): DisplayAreaNameAttr = {
    val attrValue = new NameDisplayAreaAttributeValue(attributeType)
    attrValue.setAdministrativeHierarchy(adminHierarchy)
    val adminHierarchyAttr = new NameDisplayAreaAttribute(attributeType, attrValue)
    val properties = new NamePropertyList(0.toShort, Array.empty[NameProperty])

    new DisplayAreaNameAttr(
      areaId,
      new AreaNameAttrComponents(adminHierarchyAttr, properties, emptyCondition))
  }
}
