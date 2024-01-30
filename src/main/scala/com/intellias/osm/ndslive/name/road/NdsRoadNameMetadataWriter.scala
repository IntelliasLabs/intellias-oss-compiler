package com.intellias.osm.ndslive.name.road

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{Processor, SharedProcessorData, StorageService}
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.ndslive.NdsMetadataTools
import nds.core.attributemap.ConditionTypeCodeCollection
import nds.core.conditions.ConditionTypeCode
import nds.name.attributes.NameRoadRangeAttributeType
import nds.name.instantiations.NameRoadRangeAttributeMetadata
import nds.name.metadata.RoadNameLayerContent.Values
import nds.name.metadata.RoadNameLayerMetadata
import nds.name.properties.{NamePropertyType, PropertyType}
import org.apache.spark.sql.SparkSession
import zserio.runtime.io.SerializeUtil

case class NdsRoadNameMetadataWriter(ndsConf: NdsLiveConfig, env: StorageService with LanguageService) extends Processor with NdsMetadataTools {

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    val layerMeta = new RoadNameLayerMetadata()
    layerMeta.setContent(Values.ROAD_RANGE_MAPS.or(Values.ADMIN_HIERARCHY))
    layerMeta.setAvailableLanguages(buildLanguages(env))
    layerMeta.setRoadRangeAttributeMetadata {
      new NameRoadRangeAttributeMetadata(
        buildAttributeTypes(),
        buildPropertyTypes(),
        buildConditionCollection()
      )
    }

    env.storageServices.foreach(_.save(Iterable((ndsConf.roadNameMetadata, SerializeUtil.serializeToBytes(layerMeta)))))

    data
  }

  private def buildPropertyTypes(): Array[NamePropertyType] = {
    Array(
      new NamePropertyType(PropertyType.PREFERRED_NAME, null),
      new NamePropertyType(PropertyType.USAGE_TYPE, null),
      new NamePropertyType(PropertyType.DETAIL_TYPE, null),
      new NamePropertyType(PropertyType.LANGUAGE_CODE, null)
    )
  }

  private def buildConditionCollection(): ConditionTypeCodeCollection = {
    val conditionTypes = new ConditionTypeCodeCollection()
    conditionTypes.setConditionTypeCode(Array.empty[ConditionTypeCode])
    conditionTypes
  }

  private def buildAttributeTypes(): Array[NameRoadRangeAttributeType] =
    NameRoadRangeAttributeType.values()

}
