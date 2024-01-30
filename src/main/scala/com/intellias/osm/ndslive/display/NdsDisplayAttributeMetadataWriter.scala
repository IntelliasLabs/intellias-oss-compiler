package com.intellias.osm.ndslive.display

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.compiler.display.DisplayEnvironment
import com.intellias.osm.ndslive.display.NdsDisplayAttributeMetadataWriter.content
import com.intellias.osm.ndslive.display.attributes.NdsDisplayAttributeConverter
import nds.core.attributemap.ConditionTypeCodeCollection
import nds.core.conditions.ConditionTypeCode
import nds.display.details.instantiations.{DisplayAreaAttributeMetadata, DisplayPointAttributeMetadata}
import nds.display.details.metadata.{DisplayAttributeLayerContent, DisplayAttributeLayerMetadata}
import nds.display.details.properties.DisplayPropertyType
import org.apache.spark.sql.SparkSession
import zserio.runtime.io.SerializeUtil

class NdsDisplayAttributeMetadataWriter(ndsConf: NdsLiveConfig, env: DisplayEnvironment) extends Processor {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    val layerMeta = new DisplayAttributeLayerMetadata()
    layerMeta.setContent(content)
    layerMeta.setDisplayAreaAttributeMetadata(buildAreaAttrMeta)
    layerMeta.setDisplayPointAttributeMetadata(buildPointAttrMeta)

    env.storageServices.foreach(_.save(Iterable((ndsConf.displayAttrMetaMeta, SerializeUtil.serializeToBytes(layerMeta)))))
    data
  }

  private def buildAreaAttrMeta: DisplayAreaAttributeMetadata = {
    val attributeTypes = NdsDisplayAttributeConverter.areaBuilders.map(_.attributeType)
    val attrMeta = new DisplayAreaAttributeMetadata()
    attrMeta.setAvailableAttributes(attributeTypes)
    attrMeta.setAvailableConditions(emptyConditionsList)
    attrMeta.setAvailableProperties(Array.empty[DisplayPropertyType])
    attrMeta
  }

  private def buildPointAttrMeta: DisplayPointAttributeMetadata = {
    val attributeTypes = NdsDisplayAttributeConverter.pointBuilders.map(_.attributeType)
    val attrMeta = new DisplayPointAttributeMetadata()
    attrMeta.setAvailableAttributes(attributeTypes)
    attrMeta.setAvailableConditions(emptyConditionsList)
    attrMeta.setAvailableProperties(Array.empty[DisplayPropertyType])
    attrMeta
  }

  private def emptyConditionsList: ConditionTypeCodeCollection = {
    val conditionTypes = new ConditionTypeCodeCollection()
    conditionTypes.setConditionTypeCode(Array.empty[ConditionTypeCode])
    conditionTypes
  }
}

object NdsDisplayAttributeMetadataWriter {
  def apply(ndsConf: NdsLiveConfig, env: DisplayEnvironment) = new NdsDisplayAttributeMetadataWriter(ndsConf, env)

  val content: DisplayAttributeLayerContent = DisplayAttributeLayerContent.Values.DISPLAY_AREA_SETS
}
