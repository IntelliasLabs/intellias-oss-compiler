package com.intellias.osm.compiler.poi.attributes

import com.intellias.osm.compiler.attributes.AttributeExtractorLike
import com.intellias.osm.compiler.poi.PoiEnvironment
import com.intellias.osm.model.poi.{POI, PoiStandardCategory}

case class PoiCatToAttrExtractors(poiEnv: PoiEnvironment) extends Serializable {
  val defaultExtractors: Seq[AttributeExtractorLike[POI]] = Seq(
    PoiOpen24SevenExtractor,
    PoiAcceptedPaymentMethodExtractor(poiEnv),
    PoiBrandNameExtractor(poiEnv),
    PoiNameExtractor(poiEnv)
  )

  //TODO: replace it with our class.
  val poiCatToAttExtractors: Map[PoiStandardCategory.Value, Seq[AttributeExtractorLike[POI]]] = Map(
    PoiStandardCategory.EvChargingStation -> Seq(PoiEvChargingDetailsExtractor, PoiEnergyTypeExtractor),
    PoiStandardCategory.FillingStation -> Seq(PoiEnergyTypeExtractor)
  )

  def getExtractors(standardCategories: Seq[PoiStandardCategory.Value]): Seq[AttributeExtractorLike[POI]] = {
    defaultExtractors ++ standardCategories.flatMap(sc => poiCatToAttExtractors.get(sc)).flatten
  }
}
