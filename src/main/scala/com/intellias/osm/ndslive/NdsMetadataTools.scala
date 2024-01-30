package com.intellias.osm.ndslive

import com.intellias.osm.compiler.language.{Language, LanguageService}
import nds.core.language.{AvailableLanguages, LanguageMapping, LanguageName}

trait NdsMetadataTools {
  def buildLanguages(languageService: LanguageService): AvailableLanguages = {
    val languageMappings = languageService.langService.all.map(buildLanguage).toArray
    new AvailableLanguages(languageMappings)
  }

  private def buildLanguage(l: Language):LanguageMapping = {
    val lm = new LanguageMapping()
    lm.setLanguageCode(l.langId)
    lm.setIsoCountryCode(l.isoCountryCode)
    lm.setIsoLanguageCode(l.isoLanguageCode)
    lm.setIsoScriptCode(l.isoScriptCode)
    l.isTransliterationOf.foreach(lm.setIsTransliterationOf)
    l.isDiacriticOf.foreach(lm.setIsDiacriticTransliterationOf)
    lm.setLanguageNames(l.languageNames.map(ln => new LanguageName(ln.id, ln.name)).toArray)

    lm
  }
}
