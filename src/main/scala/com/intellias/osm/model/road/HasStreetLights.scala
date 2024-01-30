package com.intellias.osm.model.road

import com.intellias.osm.model.condition.TimeCondition
import play.api.libs.json.{Format, Json}

case class HasStreetLights (timeConditions: Seq[TimeCondition] = Seq.empty)

object HasStreetLights {
  implicit val format: Format[HasStreetLights] = Json.format[HasStreetLights]
}
