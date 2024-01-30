package com.intellias.osm.compiler.road.characteristic.area
import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.compiler.road.FeatureArea
import com.intellias.osm.model.common.FeatureRange
import com.intellias.osm.model.road.{InBusinessDistrict, Topology}
import play.api.libs.json.Writes

object RoadAreaBusinessDistrictExtractor extends RoadAreaCharacteristicsExtractor[InBusinessDistrict] {
  override val tag: String                                      = "NDS:InBusinessDistrict"
  override implicit def writes: Writes[Seq[InBusinessDistrict]] = Writes.seq

  override def decodeFromOsm(topology: Topology, areas: Seq[FeatureArea]): Seq[InBusinessDistrict] = {
    val ranges: Seq[FeatureRange] = RangeBuilder.findRanges(topology, areas.filter(isBusinessArea))
    if (ranges.nonEmpty){
      Seq(InBusinessDistrict(ranges))
    } else Seq.empty
  }

  private def isBusinessArea(areas: FeatureArea): Boolean = {
    areas.tags.oneOf("landuse", Set("commercial", "industrial", "retail"))
  }
}
