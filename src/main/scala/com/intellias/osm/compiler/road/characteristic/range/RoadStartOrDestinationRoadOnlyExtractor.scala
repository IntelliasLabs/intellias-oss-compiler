package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.model.road.{StartOrDestinationRoadFlag, Topology}
import play.api.libs.json.Writes


object RoadStartOrDestinationRoadOnlyExtractor extends RoadCharacteristicsExtractor[StartOrDestinationRoadFlag] with Serializable {
  override val tag: String                             = "NDS:StartOrDestinationOnly"
  override implicit def writes: Writes[Seq[StartOrDestinationRoadFlag]] = Writes.seq

  override def decodeFromOsm(feature: Topology): Seq[StartOrDestinationRoadFlag] = {
    if(isStartOrDest(feature)) Seq(StartOrDestinationRoadFlag())
    else Seq.empty
  }

  def isStartOrDest(topology: Topology): Boolean = {
    topology.oneOf("access", Set("private" , "customers" , "no" , "permissive" , "destination" , "agricultural" ,
      "forestry" , "permit" , "unknown" , "delivery" , "military" , "restricted" , "emergency" ,
      "residents" , "official" , "employees" , "license" , "visitors" , "members"))
  }
}
