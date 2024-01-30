package com.intellias.osm.model.road

import play.api.libs.json.{Format, Json}

case class RoadAndChars(roadForm: RoadFormType, roadChars: Seq[RoadCharacterType])

object RoadAndChars {
  implicit val format: Format[RoadAndChars] = Json.format[RoadAndChars]
}