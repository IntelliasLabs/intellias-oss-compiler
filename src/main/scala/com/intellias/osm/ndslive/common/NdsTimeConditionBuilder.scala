package com.intellias.osm.ndslive.common

import com.intellias.osm.model.condition.TimeCondition.{FuzzyTime, TimeRangeOfDay}
import com.intellias.osm.model.condition.{FuzzyTimeType, TimeCondition}
import nds.core.attributemap.{Condition, ConditionList}
import nds.core.conditions.{
  ConditionTypeCode,
  ConditionValue,
  DaysOfWeek,
  FuzzyTimeDomain,
  FuzzyTimeDomainCondition,
  TimeRangeOfWeekDays,
  TimeOfDay => NdsTimeOfDay,
  TimeRangeOfDay => NdsTimeRangeOfDay
}

import java.time.{DayOfWeek, LocalTime}

object NdsTimeConditionBuilder {
  def toConditionList(conditions: Seq[TimeCondition]): ConditionList = new ConditionList(conditions.size.toShort, conditions.map(toCondition).toArray)
  def toConditionList(conditions: Array[Condition]): ConditionList = new ConditionList(conditions.size.toShort, conditions)

  def toCondition(timeCondition: TimeCondition): Condition = timeCondition match {
    case range: TimeCondition.TimeRangeOfDay     => toTimeRangeOfDay(toTimeOfDay(range))
    case TimeCondition.TimeRangeOfWeekDays(days) => toTimeRangeOfWeekDays(days)
    case TimeCondition.DaysOfWeek(days)          => toDaysOfWeek(days)
    case fuzzyTime: TimeCondition.FuzzyTime      => toFuzzyTimeRangeOfDay(fuzzyTime)
    case _                                       => throw new IllegalArgumentException(s"unsupported time condition type: ${timeCondition.getClass}")
  }

  def toTimeRangeOfDay(rangeOfDay: NdsTimeRangeOfDay): Condition = {
    val conVal = new ConditionValue(ConditionTypeCode.TIME_RANGE_OF_DAY)
    conVal.setTimeRangeOfDay(rangeOfDay)
    new Condition(ConditionTypeCode.TIME_RANGE_OF_DAY, conVal)
  }

  def toTimeOfDay(start: LocalTime, end: LocalTime, isInclusive: Boolean): NdsTimeRangeOfDay = new NdsTimeRangeOfDay(
    new NdsTimeOfDay(start.getHour.toShort, start.getMinute.toShort),
    new NdsTimeOfDay(end.getHour.toShort, end.getMinute.toShort),
    isInclusive
  )

  def toTimeOfDay(range: TimeRangeOfDay): NdsTimeRangeOfDay = new NdsTimeRangeOfDay(
    new NdsTimeOfDay(range.start.getHour.toShort, range.start.getMinute.toShort),
    new NdsTimeOfDay(range.end.getHour.toShort, range.end.getMinute.toShort),
    range.isInclusive
  )

  def toTimeRangeOfWeekDays(days: Seq[(DayOfWeek, TimeRangeOfDay)]): Condition = {
    val weekDayArray = days.foldLeft(new Array[NdsTimeRangeOfDay](7)) {
      case (acc, (day, range)) =>
        acc.update(day.ordinal(), toTimeOfDay(range))
        acc
    }

    val conVal = new ConditionValue(ConditionTypeCode.TIME_RANGE_OF_WEEKDAYS)
    conVal.setTimeRangeOfWeekDays(new TimeRangeOfWeekDays(weekDayArray))
    new Condition(ConditionTypeCode.TIME_RANGE_OF_WEEKDAYS, conVal)
  }

  def toDaysOfWeek(days: Seq[DayOfWeek]): Condition = {
    val dawOfWeek = days.foldLeft(new DaysOfWeek()) {
      case (acc, DayOfWeek(1)) => acc.setIsMonday(true); acc
      case (acc, DayOfWeek(2)) => acc.setIsTuesday(true); acc
      case (acc, DayOfWeek(3)) => acc.setIsWednesday(true); acc
      case (acc, DayOfWeek(4)) => acc.setIsThursday(true); acc
      case (acc, DayOfWeek(5)) => acc.setIsFriday(true); acc
      case (acc, DayOfWeek(6)) => acc.setIsSaturday(true); acc
      case (acc, DayOfWeek(7)) => acc.setIsSunday(true); acc
    }

    val conVal = new ConditionValue(ConditionTypeCode.DAYS_OF_WEEK)
    conVal.setDaysOfWeek(dawOfWeek)
    new Condition(ConditionTypeCode.DAYS_OF_WEEK, conVal)
  }

  def toFuzzyTimeRangeOfDay(fuzzyTime: FuzzyTime): Condition = {
    val conVal = new ConditionValue(ConditionTypeCode.FUZZY_TIME_DOMAIN)
    conVal.setFuzzyTimeDomain(new FuzzyTimeDomainCondition(toFuzzyTimeDomain(fuzzyTime.fuzzyTimeType), fuzzyTime.isInclusive))
    new Condition(ConditionTypeCode.FUZZY_TIME_DOMAIN, conVal)
  }

  def toFuzzyTimeDomain(fuzzyType: FuzzyTimeType): FuzzyTimeDomain = fuzzyType match {
    case FuzzyTimeType.External               => FuzzyTimeDomain.EXTERNAL
    case FuzzyTimeType.Dawn                   => FuzzyTimeDomain.DAWN
    case FuzzyTimeType.Dusk                   => FuzzyTimeDomain.DUSK
    case FuzzyTimeType.School                 => FuzzyTimeDomain.SCHOOL
    case FuzzyTimeType.Holiday                => FuzzyTimeDomain.HOLIDAY
    case FuzzyTimeType.Winter                 => FuzzyTimeDomain.WINTER
    case FuzzyTimeType.Spring                 => FuzzyTimeDomain.SPRING
    case FuzzyTimeType.Summer                 => FuzzyTimeDomain.SUMMER
    case FuzzyTimeType.Autumn                 => FuzzyTimeDomain.AUTUMN
    case FuzzyTimeType.HighTide               => FuzzyTimeDomain.HIGH_TIDE
    case FuzzyTimeType.LowTide                => FuzzyTimeDomain.LOW_TIDE
    case FuzzyTimeType.HighWater              => FuzzyTimeDomain.HIGH_WATER
    case FuzzyTimeType.LowWater               => FuzzyTimeDomain.LOW_WATER
    case FuzzyTimeType.Wet                    => FuzzyTimeDomain.WET
    case FuzzyTimeType.Dry                    => FuzzyTimeDomain.DRY
    case FuzzyTimeType.PeakHours              => FuzzyTimeDomain.PEAK_HOURS
    case FuzzyTimeType.OffPeakHours           => FuzzyTimeDomain.OFF_PEAK_HOURS
    case FuzzyTimeType.Morning                => FuzzyTimeDomain.MORNING
    case FuzzyTimeType.EveningRushHour        => FuzzyTimeDomain.EVENING_RUSH_HOUR
    case FuzzyTimeType.MorningRushHour        => FuzzyTimeDomain.MORNING_RUSH_HOUR
    case FuzzyTimeType.Day                    => FuzzyTimeDomain.DAY
    case FuzzyTimeType.Night                  => FuzzyTimeDomain.NIGHT
    case FuzzyTimeType.NonSchoolHours         => FuzzyTimeDomain.NON_SCHOOL_HOURS
    case FuzzyTimeType.SchoolHours            => FuzzyTimeDomain.SCHOOL_HOURS
    case FuzzyTimeType.WhenChildrenArePresent => FuzzyTimeDomain.WHEN_CHILDREN_ARE_PRESENT
    case FuzzyTimeType.SunriseTillSunset      => FuzzyTimeDomain.SUNRISE_TILL_SUNSET
    case FuzzyTimeType.SunsetTillSunrise      => FuzzyTimeDomain.SUNSET_TILL_SUNRISE
    case FuzzyTimeType.Afternoon              => FuzzyTimeDomain.AFTERNOON
    case FuzzyTimeType.Event                  => FuzzyTimeDomain.EVENT
    case FuzzyTimeType.MarketHours            => FuzzyTimeDomain.MARKET_HOURS
    case FuzzyTimeType.UndefinedOccasion      => FuzzyTimeDomain.UNDEFINED_OCCASION
    case FuzzyTimeType.RaceDays               => FuzzyTimeDomain.RACE_DAYS
    case FuzzyTimeType.Pollution              => FuzzyTimeDomain.POLLUTION
    case FuzzyTimeType.Evening                => FuzzyTimeDomain.EVENING
    case FuzzyTimeType.BusinessHours          => FuzzyTimeDomain.BUSINESS_HOURS
    case FuzzyTimeType.SkiSeason              => FuzzyTimeDomain.SKI_SEASON
    case FuzzyTimeType.TouristSeason          => FuzzyTimeDomain.TOURIST_SEASON
    case FuzzyTimeType.ChurchHours            => FuzzyTimeDomain.CHURCH_HOURS
    case FuzzyTimeType.SummerSchool           => FuzzyTimeDomain.SUMMER_SCHOOL
    case FuzzyTimeType.Funeral                => FuzzyTimeDomain.FUNERAL
    case FuzzyTimeType.HuntingSeason          => FuzzyTimeDomain.HUNTING_SEASON
    case FuzzyTimeType.MilitaryExercise       => FuzzyTimeDomain.MILITARY_EXERCISE
  }

  object DayOfWeek {
    def unapply(arg: DayOfWeek): Option[Int] = Some(arg.getValue)
  }

}
