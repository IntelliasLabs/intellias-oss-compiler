package com.intellias.osm.ndslive.poi.attributes

import com.intellias.osm.compiler.poi.attributes.PoiEnergyTypeExtractor
import com.intellias.osm.model.poi.{EnergyType, POI}
import nds.core.vehicle.{EnergyType => NdsEnergyType}
import nds.poi.attributes.{PoiAttributeType, PoiAttributeValue}
import nds.poi.instantiations.{PoiAttribute, PoiAttributeMap}
import play.api.libs.json.Json

object NdsPoiEnergyTypeBuilder extends NdsPoiAttributeBuilder {
  override val attributeTypeCode: PoiAttributeType = PoiAttributeType.FUEL_TYPE

  override def buildAttributes(pois: Array[POI]): Option[PoiAttributeMap] = {
    buildAttrMap {
      val poIdToVal = pois
        .flatMap { poi =>
          poi.tags
            .get(PoiEnergyTypeExtractor.tag)
            .map(json => poi.ndsId -> Json.parse(json).as[Seq[EnergyType]])
        }

      poIdToVal.map { case (poiId, energyTypes) =>
        val poiAttribute = toAttribute(energyTypes.flatMap(toNdsEnergyType).toArray)
        (poiId, poiAttribute, emptyProperty, emptyCondition)
      }
    }
  }

  private def toAttribute(energyTypes: Array[NdsEnergyType]): PoiAttribute = {
    val attrVal = new PoiAttributeValue(attributeTypeCode)
    attrVal.setFuelType {
      energyTypes.reduceLeft( (a, b) => a.or(b))
    }

    val attr = new PoiAttribute(attributeTypeCode)
    attr.setAttributeValue(attrVal)

    attr
  }

  private def toNdsEnergyType(osmType: EnergyType): Option[NdsEnergyType] = osmType match {
    case EnergyType.Electricity => Some(NdsEnergyType.Values.ELECTRICITY)
    case EnergyType.Cng => Some(NdsEnergyType.Values.CNG)
    case EnergyType.Diesel => Some(NdsEnergyType.Values.DIESEL)
    case EnergyType.E85 => Some(NdsEnergyType.Values.E85)
    case EnergyType.Ethanol => Some(NdsEnergyType.Values.ETHANOL)
    case EnergyType.Gasoline87 | EnergyType.Gasoline80 | EnergyType.Gasoline91 |
         EnergyType.Gasoline92 | EnergyType.Gasoline95 | EnergyType.Gasoline98 |
         EnergyType.Gasoline100 => Some(NdsEnergyType.Values.GASOLINE)
    case EnergyType.Lpg => Some(NdsEnergyType.Values.LPG)
    case _ => None
  }
}
