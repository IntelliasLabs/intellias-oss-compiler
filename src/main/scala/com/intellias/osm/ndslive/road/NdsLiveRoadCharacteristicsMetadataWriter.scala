package com.intellias.osm.ndslive.road

import com.intellias.osm.common.{Processor, SharedProcessorData, StorageService}
import com.intellias.osm.model.road.RoadAndChars
import com.intellias.osm.ndslive.road.NdsLiveRoadCharacteristicsMetadataWriter.{buildRoadMetadataLayer, toRoadTypes}
import com.intellias.osm.ndslive.road.characteristics.range.RoadTypeBuilder.{toRoadCharacter, toRoadForm}
import com.intellias.osm.tools.JsonMapperProvider
import com.intellias.osm.{NdsLiveConfig, RoadConfig}
import nds.characteristics.attributes.{CharacsRoadPositionAttributeType, CharacsRoadRangeAttributeType}
import nds.characteristics.instantiations.{CharacsRoadPositionAttributeMetadata, CharacsRoadRangeAttributeMetadata}
import nds.characteristics.metadata.{RoadCharacsLayerContent, RoadCharacteristicsLayerMetadata}
import nds.characteristics.properties.{CharacsPropertyType, PropertyType}
import nds.core.attributemap.ConditionTypeCodeCollection
import nds.core.conditions.ConditionTypeCode
import nds.core.types.RoadType
import org.apache.spark.sql.SparkSession
import play.api.libs.json.Json
import zserio.runtime.io.SerializeUtil

case class NdsLiveRoadCharacteristicsMetadataWriter(ndsConf: NdsLiveConfig, env: StorageService, roadConf: RoadConfig)
    extends Processor
    with JsonMapperProvider {

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    import spark.implicits._

    val roadFormChars: Array[RoadAndChars] = spark.read
      .parquet(roadConf.statisticPath)
      .as[String]
      .collect()
      .map(json => Json.parse(json).as[RoadAndChars])

    val layerMeta = buildRoadMetadataLayer(toRoadTypes(roadFormChars))
    env.storageServices.foreach(_.save(Iterable((ndsConf.roadCharacteristicsMetadata, SerializeUtil.serializeToBytes(layerMeta)))))

    data
  }
}
object NdsLiveRoadCharacteristicsMetadataWriter {
  def toRoadTypes(roadFormChars: Array[RoadAndChars]): Array[RoadType] = {
    roadFormChars.map { formAndChars =>
      new RoadType(
        toRoadForm(formAndChars.roadForm),
        formAndChars.roadChars.map(toRoadCharacter).toArray
      )
    }
  }

  def buildRoadMetadataLayer(roadTypes: Array[RoadType]): RoadCharacteristicsLayerMetadata = {

    val meta = new RoadCharacteristicsLayerMetadata()
    meta.setContent(RoadCharacsLayerContent.Values.ROAD_RANGE_SETS.or(RoadCharacsLayerContent.Values.ROAD_POSITION_SETS))

    val charsRoadRangAttrMeta = new CharacsRoadRangeAttributeMetadata()
    charsRoadRangAttrMeta.setAvailableAttributes(CharacsRoadRangeAttributeType.values())

    charsRoadRangAttrMeta.setAvailableProperties(
      Array(
        new CharacsPropertyType(PropertyType.STATION_STOP_TYPE, null),
        new CharacsPropertyType(PropertyType.STATION_TEMPORARY, null),
        new CharacsPropertyType(PropertyType.TOLL_PAYMENT, null)
      ))

    charsRoadRangAttrMeta.setAvailableConditions(new ConditionTypeCodeCollection(ConditionTypeCode.values()))

    meta.setRoadRangeAttributeMetadata(charsRoadRangAttrMeta)

    val charsRoadPositionAttrMeta = new CharacsRoadPositionAttributeMetadata()

    charsRoadPositionAttrMeta.setAvailableAttributes(
      Array(CharacsRoadPositionAttributeType.STOP_LINE,
            CharacsRoadPositionAttributeType.WAITING_LINE,
            CharacsRoadPositionAttributeType.SLOW_ROAD_USER_CROSSING))
    charsRoadPositionAttrMeta.setAvailableProperties(Array.empty)
    charsRoadPositionAttrMeta.setAvailableConditions(new ConditionTypeCodeCollection(ConditionTypeCode.values()))
    meta.setRoadPositionAttributeMetadata(charsRoadPositionAttrMeta)

    meta.setCoveredRoadTypes(roadTypes)

    meta
  }
}
