package com.intellias.osm.ndslive.name.display.line

import com.intellias.osm.model.display.DisplayLine
import com.intellias.osm.ndslive.name.NdsAdminTileBuilder
import nds.name.attributes.{NameDisplayLineRangeAttributeType, NameDisplayLineRangeAttributeValue}
import nds.name.instantiations.{NameDisplayLineRangeAttribute, NameDisplayLineRangeAttributeMap, NameProperty, NamePropertyList}
import nds.name.types.AdministrativeHierarchy

object NdsLineNameAdminBuilder extends NameLineAttributeMapBuilder {
  override val attributeType: NameDisplayLineRangeAttributeType = NameDisplayLineRangeAttributeType.ADMINISTRATIVE_HIERARCHY

  def buildAttributes(lines: Seq[DisplayLine], adminTileBuilder: NdsAdminTileBuilder): Option[NameDisplayLineRangeAttributeMap] = {
    val adminHierarchyRefs = lines.flatMap { line =>
      (line.rightAdminPlaces ++ line.leftAdminPlaces)
        .distinct
        .map(ap => buildAdminHierarchyRefAttribute(line.localId, adminTileBuilder.getAdminRefs(ap.adminPlaceId)))
    }
    buildAttrMap(adminHierarchyRefs)
  }

  private def buildAdminHierarchyRefAttribute(lineId: Int, adminHierarchy: AdministrativeHierarchy): DisplayLineNameAttr = {
    val attrValue = new NameDisplayLineRangeAttributeValue(attributeType)
    attrValue.setAdministrativeHierarchy(adminHierarchy)
    val adminHierarchyAttr = new NameDisplayLineRangeAttribute(attributeType, attrValue)
    val properties = new NamePropertyList(0.toShort, Array.empty[NameProperty])

    new DisplayLineNameAttr(
      lineId,
      new LineNameAttrComponents(adminHierarchyAttr, properties, emptyCondition))
  }
}
