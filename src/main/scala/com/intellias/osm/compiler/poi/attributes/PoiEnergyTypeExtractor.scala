package com.intellias.osm.compiler.poi.attributes
import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.{keyPattern, required}
import com.intellias.osm.compiler.poi.attributes.keys.OsmFuelType
import com.intellias.osm.model.poi.{EnergyType, POI}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.Writes

object PoiEnergyTypeExtractor extends PoiAttributeExtractor[EnergyType] {
  val tag = "NDS:EnergyType"
  override implicit def writes: Writes[Seq[EnergyType]] =  Writes.seq

  private val KeyPattern = keyPattern(
    "fuel",
    required(OsmFuelType)
  )


  def decodeFromOsm(poi: POI): Seq[EnergyType] = {
    poi.tags.flatMap {
      case (key, _) if key.startsWith("socket:") => Some(OsmFuelType.Electricity.energyType)
      case (key, "yes") => KeyPattern.findFirstMatchIn(key).map(OsmFuelType(_).energyType)
      case _ => None
    }.toSeq
  }

}
