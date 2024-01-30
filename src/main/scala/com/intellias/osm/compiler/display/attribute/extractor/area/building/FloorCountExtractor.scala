package com.intellias.osm.compiler.display.attribute.extractor.area.building

import com.intellias.osm.compiler.display.attribute.extractor.area.DisplayAreaAttributeExtractor
import com.intellias.osm.compiler.display.attribute.value.area.building.FloorCount
import com.intellias.osm.model.display.DisplayArea
import com.typesafe.scalalogging.StrictLogging
import play.api.libs.json.Writes

import scala.util.{Success, Try}

object FloorCountExtractor extends DisplayAreaAttributeExtractor[FloorCount] with StrictLogging {
  override def tag: String = "NDS:Building:FloorCount"

  override implicit def writes: Writes[Seq[FloorCount]] = Writes.seq

  override def decodeFromOsm(building: DisplayArea): Seq[FloorCount] = {
    building.tags.get("building:levels")
      .map(rawFloors => Try { rawFloors.toFloat })
      .collect {
        case Success(floorCount) => FloorCount(floorCount)
      }
      .toSeq
  }
}
