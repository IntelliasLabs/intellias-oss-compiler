package com.intellias.osm.ndslive.road

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{Processor, SharedProcessorData, StorageService}
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.ndslive.NdsMetadataTools
import com.typesafe.scalalogging.StrictLogging
import nds.core.attributemap.ConditionTypeCodeCollection
import nds.core.conditions.ConditionTypeCode
import nds.core.types.{RoadCharacter, RoadForm, RoadType}
import nds.rules.attributes.{RulesRoadPositionAttributeType, RulesRoadRangeAttributeType}
import nds.rules.instantiations.{RulesRoadPositionAttributeMetadata, RulesRoadRangeAttributeMetadata}
import nds.rules.metadata.{RoadRulesLayerContent, RoadRulesLayerMetadata}
import nds.rules.properties.RulesPropertyType
import org.apache.spark.sql.SparkSession
import zserio.runtime.io.SerializeUtil

case class NdsLiveRoadRulesMetadataWriter(ndsConf: NdsLiveConfig, env: StorageService with LanguageService) extends Processor
  with NdsMetadataTools with StrictLogging {


  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    val layerMeta = new RoadRulesLayerMetadata()

    layerMeta.setContent(RoadRulesLayerContent.Values.ROAD_RANGE_SETS.or(RoadRulesLayerContent.Values.ROAD_POSITION_SETS))
    layerMeta.setAvailableLanguages(buildLanguages(env))
    layerMeta.setCoveredRoadTypes(buildRoadTypes)
    layerMeta.setRoadPositionAttributeMetadata(buildPositionMetadata)
    layerMeta.setRoadRangeAttributeMetadata(buildRangeMetadata)

    env.storageServices.foreach(_.save(Iterable((ndsConf.roadRulesLayerMeta, SerializeUtil.serializeToBytes(layerMeta)))))

    data
  }

  private def buildRoadTypes: Array[RoadType] = {
    Array(
      new RoadType(RoadForm.ANY, Array.empty[RoadCharacter]),
      new RoadType(RoadForm.ANY, Array(RoadCharacter.MOTORWAY, RoadCharacter.URBAN))
    )
  }

  private def buildPositionMetadata: RulesRoadPositionAttributeMetadata = {
    val attrMeta = new RulesRoadPositionAttributeMetadata()
    attrMeta.setAvailableAttributes(Array.empty[RulesRoadPositionAttributeType])
    attrMeta.setAvailableProperties(Array.empty[RulesPropertyType])
    attrMeta.setAvailableConditions(new ConditionTypeCodeCollection(Array.empty[ConditionTypeCode]))
    attrMeta
  }

  private def buildRangeMetadata: RulesRoadRangeAttributeMetadata = {
    val attrMeta = new RulesRoadRangeAttributeMetadata()
    attrMeta.setAvailableAttributes(Array.empty[RulesRoadRangeAttributeType])
    attrMeta.setAvailableProperties(Array.empty[RulesPropertyType])
    attrMeta.setAvailableConditions(new ConditionTypeCodeCollection(Array.empty[ConditionTypeCode]))
    attrMeta
  }
}