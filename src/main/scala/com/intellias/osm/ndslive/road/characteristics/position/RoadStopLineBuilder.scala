package com.intellias.osm.ndslive.road.characteristics.position

import com.intellias.osm.compiler.road.characteristic.position.RoadPositionStopLineExtractor
import com.intellias.osm.model.road.RoadPosition
import nds.characteristics.attributes.{CharacsRoadPositionAttributeType, CharacsRoadPositionAttributeValue}
import nds.characteristics.instantiations.CharacsProperty
import nds.core.types.Flag
import play.api.libs.json.Json

object RoadStopLineBuilder extends PositionAttributeValueBuilder {
  override def getPositions(tags: Map[String, String]): Seq[RoadPosition] = {
    tags
      .get(RoadPositionStopLineExtractor.tag)
      .map( json => Json.parse(json).as[Seq[RoadPosition]])
      .getOrElse(Seq.empty)
  }

  val attrType: CharacsRoadPositionAttributeType = CharacsRoadPositionAttributeType.STOP_LINE

  val attributeValue: CharacsRoadPositionAttributeValue = {
    val attrVal = new CharacsRoadPositionAttributeValue(attrType)
    attrVal.setStopLine(new Flag())
    attrVal
  }

  override protected def attributeProperties: Array[CharacsProperty] = Array.empty[CharacsProperty]
}
