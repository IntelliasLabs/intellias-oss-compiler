package com.intellias.osm.compiler.display.attribute.extractor.point

import com.intellias.osm.compiler.display.attribute.value.point.Population
import com.intellias.osm.model.display.DisplayPoint
import play.api.libs.json.Writes

import scala.util.{Success, Try}

object PopulationExtractor extends DisplayPointAttributeExtractor[Population] {
  override implicit def writes: Writes[Seq[Population]] = Writes.seq

  override def tag: String = "NDS:Point:Population"

  override def decodeFromOsm(point: DisplayPoint): Seq[Population] = {
    point.tags.get("population")
      .map(rawPopulation => Try { rawPopulation.toInt })
      .collect {
        case Success(populationValue) => Population(populationValue)
      }
      .toSeq
  }
}
