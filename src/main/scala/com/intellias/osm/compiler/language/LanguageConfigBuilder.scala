package com.intellias.osm.compiler.language

import com.github.tototoshi.csv.CSVReader
import com.intellias.osm.AppConfig
import pureconfig.generic.auto._

import scala.io.Source
import scala.util.{Failure, Success, Using}

object LanguageConfigBuilder {
  def buildLanguages(conf: LanguageConfig): Seq[Language] = {
    val srcLangs: Seq[SrcLanguage] = AppConfig.read[SrcLanguages](Some(conf.langPath)).languages
    val langsToCountries = readConfig(conf.countryPath).tail.map { cols =>
      SrcCountryLanguage(cols.head, cols(1), cols(2), cols(3).toBoolean, cols(4).toBoolean)
    }

    val langs: Seq[Language] = langsToCountries.zipWithIndex.map {
      case (langToCont, id) =>
        val lang = srcLangs.find(l => l.isoLanguageCode == langToCont.isoLanguageCode).get
        Language(
          langId = (id+1).toShort,// 0 lang id is reserved for https://developer.nds.live/schema/core/2023.06/types/languagecode#Constant-UNDEFINED_LANGUAGE_CODE
          isoCountryCode = langToCont.isoCountryCode,
          isoLanguageCode = langToCont.isoLanguageCode,
          isoScriptCode = lang.isoScriptCode,
          isTransliterationOf = None, //TODO: implement it
          isDiacriticOf = None, //TODO: implement it
          isCountryDefault = langToCont.isDefault,
          isGlobal = langToCont.isGlobalDefault,
          languageNames = Seq.empty
        )
    }

    langs.map { lang =>
      val srcNames: Seq[SrcLangName] = srcLangs.find(l => l.isoLanguageCode == lang.isoLanguageCode).get.languageNames
      val lNames = srcNames.map(n => toLangName(n, langs))
      lang.copy(languageNames = lNames)
    }
  }

  private def toLangName(name: SrcLangName, langs: Seq[Language]): LanguageName = {
    langs.find(l => l.isoLanguageCode == name.langCode && l.isGlobal).map(l => LanguageName(l.langId, name.name)).get
  }

  def readConfig(path: String): Seq[List[String]] =
    Using(CSVReader.open(path))(_.all()) match {
      case Success(lines) => lines
      case Failure(e) => throw e
    }

  case class SrcLangName(name: String, langCode: String)

  case class SrcLanguage(isoLanguageCode: String, //ISO 639-3, with three lowercase
                         isoScriptCode: String, //ISO 15924, with first letter in uppercase and the remaining letters in lowercase
                         isTransliterationOf: Option[Short], // Indicates whether the language is a transliteration
                         isDiacriticOf: Option[Short], //Indicates whether the language is a diacritic transliteration.
                         languageNames: Seq[SrcLangName])

  case class SrcCountryLanguage(isoCountryCode: String,
                                isoSubDivisionCode: String,
                                isoLanguageCode: String,
                                isDefault: Boolean,
                                isGlobalDefault: Boolean)

  case class SrcLanguages(languages: Seq[SrcLanguage])
}