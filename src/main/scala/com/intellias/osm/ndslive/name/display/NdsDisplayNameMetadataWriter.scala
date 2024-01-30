package com.intellias.osm.ndslive.name.display

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{Processor, SharedProcessorData}
import com.intellias.osm.ndslive.NdsMetadataTools
import com.intellias.osm.ndslive.name.NdsNameEnvironment
import nds.core.attributemap.ConditionTypeCodeCollection
import nds.core.conditions.ConditionTypeCode
import nds.name.attributes.{NameDisplayAreaAttributeType, NameDisplayLineRangeAttributeType, NameDisplayPointAttributeType}
import nds.name.instantiations.{NameDisplayAreaAttributeMetadata, NameDisplayLineRangeAttributeMetadata, NameDisplayPointAttributeMetadata}
import nds.name.metadata.DisplayNameLayerContent.Values._
import nds.name.metadata.DisplayNameLayerMetadata
import nds.name.properties.{NamePropertyType, PropertyType}
import org.apache.spark.sql.SparkSession
import zserio.runtime.io.SerializeUtil

class NdsDisplayNameMetadataWriter(ndsConf: NdsLiveConfig, env: NdsNameEnvironment) extends Processor with NdsMetadataTools {
  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    val layerMeta = new DisplayNameLayerMetadata()
    layerMeta.setContent(DISPLAY_AREA_MAPS.or(DISPLAY_LINE_RANGE_MAPS).or(DISPLAY_POINT_MAPS).or(ADMIN_HIERARCHY))
    layerMeta.setAvailableLanguages(buildLanguages(env))
    layerMeta.setDisplayAreaAttributeMetadata(
      new NameDisplayAreaAttributeMetadata(
        buildAreaAttributeTypes(), buildPropertyTypes(), buildConditionCollection()
      )
    )
    layerMeta.setDisplayLineRangeAttributeMetadata(
      new NameDisplayLineRangeAttributeMetadata(
        buildLineAttributeTypes(), buildPropertyTypes(), buildConditionCollection()
      )
    )
    layerMeta.setDisplayPointAttributeMetadata(
      new NameDisplayPointAttributeMetadata(
        buildPointAttributeTypes(), buildPropertyTypes(), buildConditionCollection()
      )
    )

    env.storageServices.foreach(_.save(Iterable((ndsConf.displayNameMetadata, SerializeUtil.serializeToBytes(layerMeta)))))

    data
  }

  private def buildAreaAttributeTypes(): Array[NameDisplayAreaAttributeType] = {
    NdsDisplayNameConverter.areaNameBuilders.map(_.attributeType).toArray :+ NameDisplayAreaAttributeType.ADMINISTRATIVE_HIERARCHY
  }

  private def buildLineAttributeTypes(): Array[NameDisplayLineRangeAttributeType] = {
    NdsDisplayNameConverter.lineNameBuilders.map(_.attributeType).toArray :+ NameDisplayLineRangeAttributeType.ADMINISTRATIVE_HIERARCHY
  }

  private def buildPointAttributeTypes(): Array[NameDisplayPointAttributeType] = {
    NdsDisplayNameConverter.pointNameBuilders.map(_.attributeType).toArray
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
}

object NdsDisplayNameMetadataWriter {
  def apply(ndsConf: NdsLiveConfig, env: NdsNameEnvironment) = new NdsDisplayNameMetadataWriter(ndsConf, env)
}