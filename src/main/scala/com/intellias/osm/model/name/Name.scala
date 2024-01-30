package com.intellias.osm.model.name

import com.intellias.osm.model.common.Side
import play.api.libs.json.{Format, Json}

case class Name (name: String, isoLangCode: String, langId: Short, nameType: NameType, isDefault: Boolean, isOfficial: Boolean, side: Side = Side.Both)

object Name {
  implicit val format: Format[Name] = Json.format[Name]
}



