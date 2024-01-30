package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.road.highway
import com.intellias.osm.model.road.{RoadSurfaceWithPedestriansFlag, Topology}
import play.api.libs.json.Writes

object RoadSharedRoadSurfaceWithPedestriansExtractor extends RoadCharacteristicsExtractor[RoadSurfaceWithPedestriansFlag] with Serializable {
  override val tag: String                             = "NDS:SharedRoadSurfaceWithPedestrians"
  override implicit def writes: Writes[Seq[RoadSurfaceWithPedestriansFlag]] = Writes.seq


  override def decodeFromOsm(feature: Topology): Seq[RoadSurfaceWithPedestriansFlag] = {
    if( feature.tag(highway) && (feature.tagValue("foot", "yes") || feature.oneOf(highway, Set("residential", "living_street")) ))
      Seq(RoadSurfaceWithPedestriansFlag())
    else Seq()
  }
}
