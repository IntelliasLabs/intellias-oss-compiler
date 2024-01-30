package com.intellias.osm.model.condition

import play.api.libs.json._

import java.time.{DayOfWeek, LocalTime}
import scala.util.{Failure, Success, Try}

sealed trait TimeCondition

object TimeCondition {
  case class TimeRangeOfDay(start: LocalTime, end: LocalTime, isInclusive: Boolean = true) extends TimeCondition
  case class TimeRangeOfWeekDays(days: Seq[(DayOfWeek, TimeRangeOfDay)])
      extends TimeCondition
  case class DaysOfWeek(days: Seq[DayOfWeek]) extends TimeCondition
  case class FuzzyTime(fuzzyTimeType: FuzzyTimeType, isInclusive: Boolean = true) extends TimeCondition


  implicit val TimeRangeOfDayFormat: Format[TimeRangeOfDay]           = Json.format[TimeRangeOfDay]
  implicit val FuzzyTimeFormat: Format[FuzzyTime]                     = Json.format[FuzzyTime]

  implicit val dayOfWeekWrites: Writes[DayOfWeek] = Writes[DayOfWeek] { keyType =>
    Json.obj("type" -> keyType.name())
  }

  implicit val dayOfWeekReads: Reads[DayOfWeek] = Reads[DayOfWeek] { json =>
    (json \ "type").validate[String].flatMap { value =>
      Try(DayOfWeek.valueOf(value)) match {
        case Success(value)     => JsSuccess(value)
        case Failure(exception) => JsError(s"Unknown value: $value for java.time.DayOfWeek type, exception: ${exception.getMessage}")
      }
    }
  }

  // the order is important, should be after dayOfWeekWrites and dayOfWeekReads,
  // play json macro relies on implicit Reads and Writes for all types within DaysOfWeek
  implicit val DaysOfWeekFormat: Format[DaysOfWeek]                   = Json.format[DaysOfWeek]
//  implicit val TimeRangeOfWeekDaysMapFormat: Format[Map[DayOfWeek, TimeRangeOfWeekDays]] = Json.format[Map[DayOfWeek, TimeRangeOfWeekDays]]
  implicit val TimeRangeOfWeekDaysFormat: Format[TimeRangeOfWeekDays] = Json.format[TimeRangeOfWeekDays]

  implicit val featureRangeWrites: Writes[TimeCondition] = {
    case trOfDay: TimeRangeOfDay           => Json.obj("type" -> "TimeRangeOfDay") ++ Json.toJson(trOfDay).as[JsObject]
    case trOfWeekDays: TimeRangeOfWeekDays => Json.obj("type" -> "TimeRangeOfWeekDays") ++ Json.toJson(trOfWeekDays).as[JsObject]
    case daysOfWeek: DaysOfWeek            => Json.obj("type" -> "DaysOfWeek") ++ Json.toJson(daysOfWeek).as[JsObject]
    case fuzzyTime: FuzzyTime             => Json.obj("type" -> "FuzzyTime") ++ Json.toJson(fuzzyTime).as[JsObject]
  }

  implicit val featureRangeReads: Reads[TimeCondition] = (__ \ "type").read[String].flatMap {
    case "TimeRangeOfDay"      => TimeRangeOfDayFormat.map(identity)
    case "TimeRangeOfWeekDays" => TimeRangeOfWeekDaysFormat.map(identity)
    case "DaysOfWeek"          => DaysOfWeekFormat.map(identity)
    case "FuzzyTime"           => FuzzyTimeFormat.map(identity)
    case _                     => Reads(_ => JsError("Unknown type"))
  }
}
