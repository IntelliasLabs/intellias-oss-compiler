package com.intellias.osm.ndslive.poi

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{Processor, SharedProcessorData, StorageService}
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.ndslive.NdsMetadataTools
import nds.core.attributemap.ConditionTypeCodeCollection
import nds.core.conditions.ConditionTypeCode._
import nds.poi.attributes.PoiAttributeType
import nds.poi.instantiations.PoiAttributeMetadata
import nds.poi.metadata.{AttributeValueIconMap, PoiAttributeLayerContent, PoiAttributeLayerMetadata}
import nds.poi.properties.{PoiPropertyType, PropertyType}
import org.apache.spark.sql.SparkSession
import zserio.runtime.io.SerializeUtil

case class NdsPoiAttrMetadataWriter(ndsConf: NdsLiveConfig, env: StorageService with LanguageService) extends Processor with NdsMetadataTools {

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    val layerMeta = new PoiAttributeLayerMetadata()
    layerMeta.setContent(PoiAttributeLayerContent.Values.POI_ATTRIBUTE_MAPS)
    layerMeta.setAvailableLanguages(buildLanguages(env))
    layerMeta.setPoiAttributeMetadata(buildAttrMeta)
    layerMeta.setAttributeValueIconMap(new AttributeValueIconMap(0.toShort, Array.empty, Array.empty, Array.empty))

    env.storageServices.foreach(_.save(Iterable((ndsConf.poiAttrMeta, SerializeUtil.serializeToBytes(layerMeta)))))

    data
  }

  private def buildAttrMeta: PoiAttributeMetadata = {
    val attMeta = new PoiAttributeMetadata()
    attMeta.setAvailableAttributes(PoiAttributeType.values())

    val propType = new PoiPropertyType()
    propType.setType(PropertyType.LANGUAGE_CODE)

    attMeta.setAvailableProperties(Array(propType))

    val condCol = new ConditionTypeCodeCollection()
    condCol.setConditionTypeCode(Array(
      TIME_RANGE_OF_DAY, TIME_RANGE_OF_WEEK, DATE_RANGE_OF_YEAR, DAYS_OF_WEEK, DAYS_OF_MONTH,
      DAY_OF_YEAR, TIME_DURATION, ODD_OR_EVEN_DAYS, FUZZY_TIME_DOMAIN, TIME_RANGE_OF_WEEKDAYS,
      TIME_DURATION_HOURS, TIME_RANGE_OF_YEAR
    ))

    attMeta.setAvailableConditions(condCol)
    attMeta
  }
}
