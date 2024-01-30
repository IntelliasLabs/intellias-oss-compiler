package com.intellias.osm.compiler

package object language {
  case class LanguageName(id: Short, name: String)
  case class Language(langId: Short,
                      isoCountryCode: String, //ISO-3166-1 alpha 3 code, with three uppercase
                      isoLanguageCode: String, //ISO 639-3, with three lowercase
                      isoScriptCode: String, //ISO 15924, with first letter in uppercase and the remaining letters in lowercase
                      isTransliterationOf: Option[Short], // Indicates whether the language is a transliteration
                      isDiacriticOf: Option[Short], //Indicates whether the language is a diacritic transliteration.
                      isCountryDefault: Boolean,
                      isGlobal: Boolean,
                      languageNames: Seq[LanguageName])

  case class LanguageConfig(langPath: String, countryPath: String)

}
