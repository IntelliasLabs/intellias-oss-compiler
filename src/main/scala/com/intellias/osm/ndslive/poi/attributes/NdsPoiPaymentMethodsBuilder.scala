package com.intellias.osm.ndslive.poi.attributes

import com.intellias.osm.compiler.poi.attributes.PoiAcceptedPaymentMethodExtractor
import com.intellias.osm.model.poi.{POI, Payment}
import nds.poi.attributes.PoiAttributeType.ACCEPTED_PAYMENT_METHODS
import nds.poi.attributes.{PoiAttributeType, PoiAttributeValue}
import nds.poi.instantiations.{PoiAttribute, PoiAttributeMap}
import nds.poi.types.{AcceptedPaymentMethods, PaymentMethod}
import play.api.libs.json.Json

object NdsPoiPaymentMethodsBuilder extends NdsPoiAttributeBuilder {
  override val attributeTypeCode: PoiAttributeType = ACCEPTED_PAYMENT_METHODS

  override def buildAttributes(pois: Array[POI]): Option[PoiAttributeMap] =
    buildAttrMap {
      pois.flatMap { poi =>
        poi.tags
          .get(PoiAcceptedPaymentMethodExtractor.tag)
          .map(json => poi.ndsId -> Json.parse(json).as[Seq[Payment]])
      }.map {
        case (poiId, payments) =>
          val poiAttribute = buildAttr(payments.map(_.name))
          val propertyList = buildLanguagePropertyList(payments.map(_.langId))
          (poiId, poiAttribute, propertyList, emptyCondition)
      }
    }

  def buildAttr(payments: Seq[String]): PoiAttribute = {
    val attrVal = new PoiAttributeValue(attributeTypeCode)

    val accPayMethods = new AcceptedPaymentMethods(
      payments.size.toShort,
      payments.map { payName =>
        val payMethod = new PaymentMethod()
        payMethod.setName(payName)
        payMethod
      }.toArray
    )

    attrVal.setAcceptedPaymentMethods(accPayMethods)

    val attr = new PoiAttribute(attributeTypeCode)
    attr.setAttributeValue(attrVal)

    attr
  }
}
