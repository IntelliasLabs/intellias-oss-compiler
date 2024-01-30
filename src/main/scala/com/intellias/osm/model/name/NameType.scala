package com.intellias.osm.model.name

import play.api.libs.json.{JsError, JsSuccess, Json, Reads, Writes}

sealed trait NameType

object NameType {
  val values: Seq[NameType] =  Seq(
    Name,
    OfficialName,
    InternationalName,
    LocalName,
    OldName,
    RegionalName,
    ShortName,
    Alternate,
    NickName,
    SortingName
  )

  case object Name extends NameType
  case object OfficialName extends NameType
  case object InternationalName extends NameType
  case object LocalName extends NameType
  case object OldName extends NameType
  case object RegionalName extends NameType
  case object ShortName extends NameType
  case object SortingName extends NameType
  case object Alternate extends NameType
  case object NickName extends NameType

  implicit val writers: Writes[NameType] = Writes[NameType] { keyType =>
    Json.obj("type" -> keyType.getClass.getSimpleName)
  }

  implicit val readers: Reads[NameType] = Reads[NameType] { json =>
    (json \ "type").validate[String].flatMap { value =>
      this.values.find(_.getClass.getSimpleName == value)
        .map(JsSuccess(_))
        .getOrElse(JsError(s"Unknown ${this.getClass.getName} type: $value"))
    }
  }
}