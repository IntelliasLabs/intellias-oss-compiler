package com.intellias.osm.compiler.poi.attributes
import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.{keyPattern, required}
import com.intellias.osm.compiler.language.LanguageResolver
import com.intellias.osm.compiler.poi.PoiEnvironment
import com.intellias.osm.compiler.poi.attributes.keys.PaymentType
import com.intellias.osm.compiler.poi.attributes.keys.PaymentType.LocalPayment
import com.intellias.osm.model.poi.{POI, Payment}
import play.api.libs.json.Writes


case class PoiAcceptedPaymentMethodExtractor(env: PoiEnvironment) extends PoiAttributeExtractor[Payment] with LanguageResolver{
  val tag: String = PoiAcceptedPaymentMethodExtractor.tag
  override implicit def writes: Writes[Seq[Payment]] = Writes.seq

  private val KeyPattern = keyPattern(
    "payment",
    required(PaymentType)
  )

  def decodeFromOsm(poi: POI): Seq[Payment] = {
    poi.tags.flatMap {
      case (key, value) => KeyPattern.findFirstMatchIn(key)
        .map {rm =>
          (PaymentType(rm), value)
        }
    }.flatMap {
      case (LocalPayment(localPaymentName), "yes" | "only") =>
        Seq(Payment(localPaymentName, getRegionOrDefaultLang(env.langService)(poi.adminPlace).langId))
      case (paymentType, "yes" | "only") =>
        paymentType.names.flatMap { pn =>
          getLanguage(env.langService)(pn.langIsoCode, poi.adminPlace)
            .map(l => Payment(pn.name, l.langId))
        }
      case _ => Seq.empty
    }.toSeq
  }
}

object PoiAcceptedPaymentMethodExtractor {
  val tag = "NDS:PoiPaymentMethod"
}