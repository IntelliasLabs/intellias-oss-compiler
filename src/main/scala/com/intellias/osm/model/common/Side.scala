package com.intellias.osm.model.common

import play.api.libs.json._

sealed trait Side

object Side {
  val values: Seq[Side with Serializable] = Seq(Both, Left, Right)

  case object Both extends Side
  case object Left extends Side
  case object Right extends Side

  implicit val compositeKeyWriters: Writes[Side] = Writes[Side] { keyType =>
    Json.obj("type" -> keyType.getClass.getSimpleName)
  }

  implicit val compositeKeyReaders: Reads[Side] = Reads[Side] { json =>
    (json \ "type").validate[String].flatMap { value =>
      this.values
        .find(_.getClass.getSimpleName == value)
        .map(JsSuccess(_))
        .getOrElse(JsError(s"Unknown ${this.getClass.getName} type: $value"))
    }
  }
}
