package com.intellias.osm.compiler.language

import com.neovisionaries.i18n.LanguageCode

import scala.util.{Success, Try}

case class Iso639(language: LanguageCode) {
  def alpha3: String = language.getAlpha3.name()
  def alpha2: String = language.name()
}

object Iso639 {
  def toIso639_3(code: String): Option[Iso639] = Try {
    LanguageCode.getByCodeIgnoreCase(code)
  } match {
    case Success(value) if value != null => Some(Iso639(value))
    case _ => None
  }

  def unapply(str: String): Option[Iso639] = toIso639_3(str)
}