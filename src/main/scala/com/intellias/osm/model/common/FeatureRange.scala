package com.intellias.osm.model.common

import com.intellias.mobility.geo.tools.common.Wgs84Coordinate
import com.intellias.osm.model.common.all._
import play.api.libs.json._

sealed trait FeatureRange

object FeatureRange {
  case object Complete                                                   extends FeatureRange
  case class PositionRange(start: Wgs84Coordinate, end: Wgs84Coordinate) extends FeatureRange

  implicit val positionRangeWrites: Writes[PositionRange] = Json.writes[PositionRange]
  implicit val positionRangeReads: Reads[PositionRange]   = Json.reads[PositionRange]

  implicit val featureRangeWrites: Writes[FeatureRange] = {
    case Complete          => Json.obj("type" -> "Complete")
    case pr: PositionRange => Json.obj("type" -> "PositionRange") ++ Json.toJson(pr).as[JsObject]
  }

  implicit val featureRangeReads: Reads[FeatureRange] = (__ \ "type").read[String].flatMap {
    case "Complete"      => Reads.pure(Complete)
    case "PositionRange" => positionRangeReads.map(identity)
    case _               => Reads(_ => JsError("Unknown type"))
  }

}
