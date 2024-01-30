package com.intellias.osm.compiler.attributes.keys

import com.intellias.osm.compiler.language.Iso639
import play.api.libs.json._

sealed abstract class OsmLanguage(val value: String) {
  override def toString: String = value
}

case object OsmLanguage extends OsmCompositeKey[OsmLanguage] {
  override val values: Seq[OsmLanguage] = Seq {new OsmLanguage("[a-zA-Z]{2,3}") {}}

  override def groupName: String = "language"
  override def default: OsmLanguage = LocalLanguage

  case object LocalLanguage extends OsmLanguage("") //applied to attributes without lang code.
  case class LanguageCode(language: String) extends OsmLanguage("") // parsed language code iso-639-alpha3
  case object NotALanguage extends OsmLanguage("") // others attributes but not a language

  override def apply(value: String): OsmLanguage = {
    if(value == "") LocalLanguage
    else value match {
      case Iso639(langCode) => LanguageCode(langCode.alpha3)
      case _ => NotALanguage
    }
  }

  implicit val osmLanguageReads: Reads[OsmLanguage] = Reads { json =>
    val langType = (json \ "type").asOpt[String].getOrElse("")
    langType match {
      case "local" => JsSuccess(OsmLanguage.LocalLanguage)
      case "lang" =>
        val langCode = (json \ "code").asOpt[String].getOrElse("")
        OsmLanguage(langCode) match {
          case lc: OsmLanguage.LanguageCode => JsSuccess(lc)
          case OsmLanguage.NotALanguage => JsSuccess(OsmLanguage.NotALanguage)
          case _ => JsError("Invalid language value")
        }
      case "notLang" => JsSuccess(OsmLanguage.NotALanguage)
      case _ => JsError("Unknown type")
    }
  }

  implicit val osmLanguageWrites: Writes[OsmLanguage] = Writes {
    case OsmLanguage.LocalLanguage => Json.obj("type" -> "local")
    case OsmLanguage.LanguageCode(lang) => Json.obj("type" -> "lang", "code" -> lang)
    case OsmLanguage.NotALanguage => Json.obj("type" -> "notLang")
  }
}
