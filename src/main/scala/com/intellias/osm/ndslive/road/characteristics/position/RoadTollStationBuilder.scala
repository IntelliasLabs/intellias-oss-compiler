package com.intellias.osm.ndslive.road.characteristics.position

import com.intellias.osm.compiler.road.characteristic.position.{RoadPositionStopLineExtractor, RoadPositionTollStationExtractor}
import com.intellias.osm.model.road.RoadPosition
import nds.characteristics.attributes.{CharacsRoadPositionAttributeType, CharacsRoadPositionAttributeValue}
import nds.characteristics.instantiations.CharacsProperty
import nds.characteristics.types.StationaryObjectType
import play.api.libs.json.Json

object RoadTollStationBuilder extends PositionAttributeValueBuilder {
  override def getPositions(tags: Map[String, String]): Seq[RoadPosition] = {
    tags
      .get(RoadPositionTollStationExtractor.tag)
      .map( json => Json.parse(json).as[Seq[RoadPosition]])
      .getOrElse(Seq.empty)
  }

  val attrType: CharacsRoadPositionAttributeType = CharacsRoadPositionAttributeType.STATION

  val attributeValue: CharacsRoadPositionAttributeValue = {
    val attrVal = new CharacsRoadPositionAttributeValue(attrType)
    attrVal.setStationaryObjectType(StationaryObjectType.TOLL_STATION)
    attrVal
  }

  override protected def attributeProperties: Array[CharacsProperty] = ???
}
