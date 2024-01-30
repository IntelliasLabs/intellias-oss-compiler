package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.condition.OsmOpeningHoursToCondition
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.model.condition.TimeCondition
import com.intellias.osm.model.road.{HasStreetLights, Topology}
import play.api.libs.json.Writes

import scala.util.Success

object RoadHasStreetLightsExtractor extends RoadCharacteristicsExtractor[HasStreetLights] with Serializable {
  override val tag: String                             = "NDS:HasStreetLights"
  override implicit def writes: Writes[Seq[HasStreetLights]] = Writes.seq


  override def decodeFromOsm(feature: Topology): Seq[HasStreetLights] = {
    feature.tags.get("lit") match {
      case Some("disused" | "no") | None => Seq.empty
      case Some("yes" | "automatic" | "limited" | "interval") => Seq(HasStreetLights())
      case Some(value) => Seq(HasStreetLights(getTimeCondition(value)))
    }
  }


  private def getTimeCondition(timeCond: String): Seq[TimeCondition] = {
    OsmOpeningHoursToCondition.buildCondition(timeCond) match {
      case Success(conList) if conList.nonEmpty => conList.toSeq
      case _ => Seq.empty
    }
  }
}
