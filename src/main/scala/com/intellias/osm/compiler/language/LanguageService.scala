package com.intellias.osm.compiler.language

trait LanguageService {
  val langService: LanguageService.Service
}

object LanguageService {
  trait Service extends Serializable {
    def all: Seq[Language]
    def countryDefault(isoCountry: String): Option[Language]
    def countryOfficials(isoCountry: String): Seq[Language]
    def regionDefault(isoCountry: String, isoSubrb: List[String]): Option[Language]
    def regionOfficials(isoCountry: String, isoSubrb: String): Seq[Language]
    def globalDefault(isoLanguage: String): Option[Language]
    def defaultLanguage: Language
    def getCountryOrGlobal(isoCountry: String, isoLanguage: String): Option[Language]
  }

  def apply(conf: LanguageConfig): LanguageService = new LanguageService() {
    override val langService: Service = LanguageServiceImpl(conf)
  }
}


class LanguageServiceImpl(languages: Seq[Language]) extends LanguageService.Service {
  override def all: Seq[Language] = languages

  override def countryDefault(isoCountry: String): Option[Language] = languages.find(l => l.isoCountryCode == isoCountry && l.isCountryDefault)

  override def countryOfficials(isoCountry: String): Seq[Language] = languages.filter(l => l.isoCountryCode == isoCountry)

  override def regionDefault(isoCountry: String, isoSubrb: List[String]): Option[Language] = countryDefault(isoCountry) //TODO: implement this.

  override def regionOfficials(isoCountry: String, isoSubrb: String): Seq[Language] = countryOfficials(isoCountry) //TODO: implement this.

  override def globalDefault(isoLanguage: String): Option[Language] = languages.find(l => l.isoLanguageCode == isoLanguage && l.isGlobal)

  override def defaultLanguage: Language = globalDefault("eng").get

  override def getCountryOrGlobal(isoCountry: String, isoLanguage: String): Option[Language] = {
    countryOfficials(isoCountry).find(l => l.isoLanguageCode == isoLanguage).orElse(globalDefault(isoLanguage))
  }
}

object LanguageServiceImpl {
  def apply(conf: LanguageConfig): LanguageServiceImpl = new LanguageServiceImpl(LanguageConfigBuilder.buildLanguages(conf))
}