package com.intellias.osm.ndslive.poi.attributes

import com.intellias.osm.compiler.poi.attributes.PoiBrandNameExtractor
import com.intellias.osm.model.poi.{Brand, POI}
import nds.poi.attributes.PoiAttributeType.BRAND_NAME
import nds.poi.attributes.{PoiAttributeType, PoiAttributeValue}
import nds.poi.instantiations.{PoiAttribute, PoiAttributeMap}
import play.api.libs.json.Json

object NdsPoiBrandBuilder extends NdsPoiAttributeBuilder {
  override val attributeTypeCode: PoiAttributeType = BRAND_NAME

  override def buildAttributes(pois: Array[POI]): Option[PoiAttributeMap] = {
    buildAttrMap {
      pois.flatMap { poi =>
        poi.tags
          .get(PoiBrandNameExtractor.tag)
          .map(json => Json.parse(json).as[Seq[Brand]].map(poi.ndsId -> _))
      }.flatten.map {
        case (poiId, brand) =>
          val poiAttribute = buildAttr(brand.name)
          val propertyList = buildLanguagePropertyList(Seq(brand.langId))
          (poiId, poiAttribute, propertyList, emptyCondition)
      }
    }
  }

  def buildAttr(name: String): PoiAttribute = {
    val attrVal = new PoiAttributeValue(attributeTypeCode)
    attrVal.setBrandName(name)

    val attr = new PoiAttribute(attributeTypeCode)
    attr.setAttributeValue(attrVal)

    attr
  }

}
