package com.intellias.osm.model

import play.api.libs.json._

trait JsonFormatter[T] {
  val values: Seq[T]

  implicit val writers: Writes[T] = Writes[T] { keyType =>
    Json.obj("type" -> keyType.getClass.getSimpleName)
  }

  implicit val readers: Reads[T] = Reads[T] { json =>
    (json \ "type").validate[String].flatMap { value =>
      this.values.find(_.getClass.getSimpleName == value)
        .map(JsSuccess(_))
        .getOrElse(JsError(s"Unknown ${this.getClass.getName} type: $value"))
    }
  }
}
