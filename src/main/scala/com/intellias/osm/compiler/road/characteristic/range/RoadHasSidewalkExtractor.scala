package com.intellias.osm.compiler.road.characteristic.range

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.{keyPattern, optional}
import com.intellias.osm.compiler.attributes.keys.OsmSide
import com.intellias.osm.compiler.road.characteristic.RoadCharacteristicsExtractor
import com.intellias.osm.model.road.{HasSideWalk, Topology}
import play.api.libs.json.Writes

object RoadHasSidewalkExtractor extends RoadCharacteristicsExtractor[HasSideWalk] with Serializable {
  override val tag: String                             = "NDS:HasSidewalk"
  override implicit def writes: Writes[Seq[HasSideWalk]] = Writes.seq

  private val KeyPattern = keyPattern("sidewalk", optional(OsmSide))

  override def decodeFromOsm(feature: Topology): Seq[HasSideWalk] =
    feature.tags.flatMap {
      case (key, value) => KeyPattern.findFirstMatchIn(key)
        .map(m => (OsmSide(m), value))
        .map {
          case (_, "no" | "none") => Seq.empty[HasSideWalk]
          case (OsmSide.Both, value) => Seq(HasSideWalk(side = OsmSide(value).side))
          case (osmSide, _) => Seq(HasSideWalk(osmSide.side))
        }
    }.flatten.toSeq
}
