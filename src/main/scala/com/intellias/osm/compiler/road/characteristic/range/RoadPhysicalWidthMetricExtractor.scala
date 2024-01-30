package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.schema.model.road.PhysicalWidth
import com.intellias.osm.model.road.Topology
import play.api.libs.json.Writes

import scala.util.{Success, Try}

object RoadPhysicalWidthMetricExtractor extends RoadCharacteristicsExtractor[PhysicalWidth] with Serializable {
  val tag                                                  = "NDS:PhysicalWidth"
  override implicit def writes: Writes[Seq[PhysicalWidth]] = Writes.seq

  private val key = "width"

  override def decodeFromOsm(topology: Topology): Seq[PhysicalWidth] =
    topology.tags
      .get(key)
      .flatMap(parseWith)
      .map(w => Seq(PhysicalWidth(w)))
      .getOrElse(Seq.empty)


  def parseWith(value: String): Option[Int] =
    Try(value.toInt) match {
      case Success(width) if width > 0 => Some(width)
      case _ => None
    }
}
