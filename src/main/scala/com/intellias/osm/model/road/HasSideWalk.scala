package com.intellias.osm.model.road

import com.intellias.osm.model.common.Side
import play.api.libs.json.{Format, Json}

case class HasSideWalk(side: Side = Side.Both)

object HasSideWalk {
  implicit val format: Format[HasSideWalk] = Json.format[HasSideWalk]
}
