package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.compiler.road.highway
import com.intellias.osm.model.road.{RoadFormType, Topology}
import play.api.libs.json.Writes

object RoadTypeFormExtractor extends RoadCharacteristicsExtractor[RoadFormType] with Serializable {
  override val tag: String                             = "NDS:RoadForm"
  override implicit def writes: Writes[Seq[RoadFormType]] = Writes.seq

  override def decodeFromOsm(feature: Topology): Seq[RoadFormType] =
    Seq {
      feature match {
        case topo if isRoundAbout(topo)      => RoadFormType.Roundabout
        case topo if isMotorwayRamp(topo)    => RoadFormType.Ramp
        case topo if isDualCarriageway(topo) => RoadFormType.DualCarriageway
        case topo if isSlipRoad(topo)        => RoadFormType.SlipRoad
        case topo if isPedestrian(topo)      => RoadFormType.PedestrianWay
        case topo if isServiceRoad(topo)     => RoadFormType.ServiceRoad
        case topo if isAny(topo)             => RoadFormType.Any
        case _                               => RoadFormType.Normal
      }
    }


  private def isRoundAbout(topo: Topology): Boolean = topo.tagValue("junction", "roundabout") || topo.tagValue("junction", "mini_roundabout")

  def isPedestrian(topo: Topology): Boolean =
    topo.oneOf(highway, Set("footway", "steps", "corridor")) ||
      topo.allOff(highway -> "cycleway", "foot" -> "designated") ||
      topo.allOff(highway -> "path", "foot"     -> "designated")

  private def isSlipRoad(topo: Topology): Boolean = topo.oneOf(highway, Set("trunk_link", "primary_link", "secondary_link", "tertiary_link"))

  private def isMotorwayRamp(topo: Topology): Boolean = topo.tagValue(highway, "motorway_link")

  def isServiceRoad(topo: Topology): Boolean = topo.tag(highway) && topo.oneOf("frontage_road" -> "yes", "side_road" -> "yes")

  //TODO: need investigation.
  def isDualCarriageway(topo: Topology): Boolean =
    topo.tagValue("dual_carriageway", "yes") || topo.oneOf(highway, Set("motorway", "trunk", "primary")) //TODO: add expressway here.
  def isAny(topo: Topology): Boolean = topo.oneOf(highway, Set("path", "track"))

//  def toRoadForm(tags: Map[String, String]): RoadForm = {
//    tags.find { case (k, _) => k.startsWith(prefix) }.map { case (k, _) => RoadForm.valueOf(k.replace(prefix, "")) }
//      .getOrElse(RoadForm.ANY)
//  }
//
//  private def toKeyValue(roadForm: RoadForm): (String, String) = {
//    (s"$prefix${roadForm.name()}", "")
//  }
}
