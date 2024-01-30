package com.intellias.osm.ndslive.poi.attributes

import com.intellias.osm.model.poi.POI
import com.intellias.osm.ndslive.poi.attributes.NdsPoiBrandBuilder.PoiId
import nds.poi.attributes.{PoiAttributeType, PoiAttributeValue}
import nds.poi.instantiations.{PoiAttribute, PoiAttributeMap}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.language.postfixOps

class NdsPoiAttributeBuilderTest extends AnyFunSpec with Matchers {

  it("should properly build PoiAttributeMap") {
    val builder = new NdsBuilderStub(
      Seq((5, "Brand1", 3),
        (4, "Brand2", 1),
        (3, "Brand3", 1),
        (2, "Brand1", 3),
        (1, "Brand2", 1))
    )
    val Some(attrxMap) = builder.buildAttributes()

    attrxMap.getFeature shouldBe 5
    attrxMap.getAttribute shouldBe 3
    attrxMap.getAttributeTypeCode shouldBe builder.attributeTypeCode

    attrxMap.getFeatureReferences.head shouldBe 5
    attrxMap.getAttributeValues.toSeq(attrxMap.getFeatureValuePtr.toSeq.head).getAttributeValue.getBrandName shouldBe "Brand1"
    attrxMap.getAttributeProperties.toSeq(attrxMap.getFeatureValuePtr.toSeq.head).getProperty.head.getPropertyValue.getValue.getLanguageCode shouldBe 3

    attrxMap.getFeatureReferences.toSeq(1) shouldBe 4
    attrxMap.getAttributeValues.toSeq(attrxMap.getFeatureValuePtr.toSeq(1)).getAttributeValue.getBrandName shouldBe "Brand2"
    attrxMap.getAttributeProperties.toSeq(attrxMap.getFeatureValuePtr.toSeq(1)).getProperty.head.getPropertyValue.getValue.getLanguageCode shouldBe 1
  }

  it("should return None when attributes map is empty") {
    val builder = new NdsBuilderStub(Seq.empty)
    builder.buildAttributes() shouldBe None
  }



  private class NdsBuilderStub(data: Seq[(PoiId, String, Short)]) extends NdsPoiAttributeBuilder {
    override val attributeTypeCode: PoiAttributeType = PoiAttributeType.BRAND_NAME

    override def buildAttributes(pois: Array[POI] = Array.empty): Option[PoiAttributeMap] =
      buildAttrMap {
        data.map {
          case (poiId, name, langId) =>
            (poiId, buildAttr(name), buildLanguagePropertyList(Seq(langId)), emptyCondition)
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
}
