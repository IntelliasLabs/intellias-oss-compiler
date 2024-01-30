package com.intellias.osm.compiler.condition

import ch.poole.openinghoursparser._
import com.intellias.osm.compiler.condition.OsmTimeOps._
import com.intellias.osm.model.condition.{FuzzyTimeType, TimeCondition}
import com.intellias.osm.model.condition.TimeCondition.{DaysOfWeek, FuzzyTime, TimeRangeOfDay, TimeRangeOfWeekDays}

import java.io.ByteArrayInputStream
import java.time.{DayOfWeek, LocalTime}
import scala.jdk.CollectionConverters._
import scala.util.Try

//TODO: only few types are supported.
// Temporary solution with 2 as a suffix, will be renamed after RoadCarpoolExtractor and RoadHasStreetLightsExtractor refactoring.
object OsmOpeningHoursToCondition {

  def buildCondition(openingHoursStr: String): Try[Iterable[TimeCondition]] = Try {
    //conditional statement may be enclosed by brackets
    //parser fails at them
    val timeStr= if (openingHoursStr.startsWith("(")){
      openingHoursStr.substring(1,openingHoursStr.length-1)
    } else {
      openingHoursStr
    }
    val parser = new OpeningHoursParser(new ByteArrayInputStream(timeStr.getBytes()))
    parser.rules(true, true).asScala.flatMap { rule =>
      ruleToCondition(rule)
    }.toSeq
  }

  private[condition] def ruleToCondition(rule: Rule): Iterable[TimeCondition] =
    (Option(rule.getDays.asScala).filterNot(_.isEmpty), Option(rule.getTimes.asScala).filterNot(_.isEmpty), rule.isTwentyfourseven) match {
      case (Some(days), Some(times), _) if times.isAllTime =>
        toTimeRangeOfWeekDays(days, times)
      case (Some(days), Some(times), _) if times.isAllEvent =>
        days.map(toDaysOfWeek) ++ toFuzzyTimeRangeOfDay(times)
      case (_, Some(times), _) if times.isAllTime =>
        toTimeOfDay(times)
      case (_, Some(times), _) if times.isAllEvent =>
        toFuzzyTimeRangeOfDay(times)
      case (_, _, true) => to24_7
      case _ =>
        Seq.empty
    }

  private def to24_7 = Seq(TimeRangeOfDay(LocalTime.of(0, 0), LocalTime.of(23, 59)))



  private[condition] def toFuzzyTimeRangeOfDay(times: Iterable[TimeSpan]): Iterable[FuzzyTime] = {
    times.flatMap {
      case TimeSpanEventUn(Event.SUNRISE | Event.DAWN, Event.SUNSET | Event.DUSK) =>
        Some(FuzzyTime(FuzzyTimeType.SunriseTillSunset))
      case TimeSpanEventUn(Event.SUNSET | Event.DUSK, Event.SUNRISE | Event.DAWN) =>
        Some(FuzzyTime(FuzzyTimeType.SunsetTillSunrise))
      case _ => None
    }
  }



  private[condition] def toTimeRangeOfWeekDays(days: Iterable[WeekDayRange], hours: Iterable[TimeSpan]): Iterable[TimeRangeOfWeekDays] = {
    for {
      time <- toTimeOfDay(hours)
      weekDays <- days.map(days => days.getStartDay.ordinal() to days.getEndDay.ordinal())
    } yield {
      TimeRangeOfWeekDays{
        weekDays.map { dayNum =>
          DayOfWeek.of(dayNum + 1) -> time
        }
      }
    }
  }

  private[condition] def toDaysOfWeek(weekRange: WeekDayRange): DaysOfWeek = {
    DaysOfWeek{
      (weekRange.getStartDay.ordinal() to weekRange.getEndDay.ordinal())
        .map(dayNum => DayOfWeek.of(dayNum + 1))
    }
  }



  private[condition] def toTimeOfDay(times: Iterable[TimeSpan]): Iterable[TimeRangeOfDay] = {
    times.map{ ts =>
      val (sH, sM) = convertMinutes(ts.getStart)
      val (eH, eM) = convertMinutes(ts.getEnd)
      TimeRangeOfDay(LocalTime.of(sH, sM), LocalTime.of(eH, eM))
    }
  }


  private[condition] def convertMinutes(minutesFromMidnight: Int): (Short, Short) = {
    val hours = minutesFromMidnight / 60
    val minutes = minutesFromMidnight % 60
    (hours.toShort, minutes.toShort)
  }
}
