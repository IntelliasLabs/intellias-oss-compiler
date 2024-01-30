package com.intellias.osm.ndslive.display.attributes.point

import com.intellias.osm.compiler.display.attribute.extractor.point.PopulationExtractor
import com.intellias.osm.compiler.display.attribute.value.point.Population
import com.intellias.osm.model.display.{DisplayFeatureType, DisplayPoint}
import nds.display.details.attributes.{DisplayPointAttributeType, DisplayPointAttributeValue}
import nds.display.details.instantiations._
import nds.display.details.types.Population._
import nds.display.details.types.{Population => NdsPopulation}
import play.api.libs.json.Json

object NdsCityPopulationBuilder extends NdsDisplayPointAttributeBuilder[Population] with Serializable {
  override val sourceTag: String = PopulationExtractor.tag
  override val attributeType: DisplayPointAttributeType = DisplayPointAttributeType.POPULATION

  override protected def parseSourceAttribute(sourceAttrJson: String): Seq[Population] = {
    Json.parse(sourceAttrJson).as[Seq[Population]]
  }

  override protected def toFullAttribute(sourcePopulation: Population): DisplayPointFullAttribute = {
    val attrValue = new DisplayPointAttributeValue(attributeType)
    attrValue.setPopulation(mapPopulation(sourcePopulation.value))
    new DisplayPointFullAttribute(
      attrValue.getType, attrValue, emptyProperties, emptyConditions
    )
  }

  private def mapPopulation(populationValue: Int): NdsPopulation = {
    populationValue match {
      case p if                    p < 5000       => POPULATION_SMALL
      case p if 5000       <= p && p < 10000      => POPULATION_5K
      case p if 10000      <= p && p < 50000      => POPULATION_10K
      case p if 50000      <= p && p < 100_000    => POPULATION_50K
      case p if 100_000    <= p && p < 500_000    => POPULATION_100K
      case p if 500_000    <= p && p < 1_000_000  => POPULATION_500K
      case p if 1_000_000  <= p && p < 5_000_000  => POPULATION_1M
      case p if 5_000_000  <= p && p < 10_000_000 => POPULATION_5M
      case p if 10_000_000 <= p && p < 15_000_000 => POPULATION_10M
      case p if 15_000_000 <= p && p < 20_000_000 => POPULATION_15M
      case p if 20_000_000 <= p && p < 25_000_000 => POPULATION_20M
      case p if 25_000_000 <= p && p < 30_000_000 => POPULATION_25M
      case p if 30_000_000 <= p && p < 35_000_000 => POPULATION_30M
      case p if 35_000_000 <= p && p < 40_000_000 => POPULATION_35M
      case p if 40_000_000 <= p                   => POPULATION_40M
    }
  }

  override protected def filter(point: DisplayPoint): Boolean = {
    point.featureType == DisplayFeatureType.PointCityCenter.name
  }
}
