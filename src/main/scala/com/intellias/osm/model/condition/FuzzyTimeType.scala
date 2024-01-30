package com.intellias.osm.model.condition

import com.intellias.osm.model.JsonFormatter

sealed trait FuzzyTimeType

object FuzzyTimeType extends JsonFormatter[FuzzyTimeType] {
  override val values: Seq[FuzzyTimeType] = Seq(
    External,
    Dawn,
    Dusk,
    School,
    Holiday,
    Winter,
    Spring,
    Summer,
    Autumn,
    HighTide,
    LowTide,
    HighWater,
    LowWater,
    Wet,
    Dry,
    PeakHours,
    OffPeakHours,
    Morning,
    EveningRushHour,
    MorningRushHour,
    Day,
    Night,
    NonSchoolHours,
    SchoolHours,
    WhenChildrenArePresent,
    SunriseTillSunset,
    SunsetTillSunrise,
    Afternoon,
    Event,
    MarketHours,
    UndefinedOccasion,
    RaceDays,
    Pollution,
    Evening,
    BusinessHours,
    SkiSeason,
    TouristSeason,
    ChurchHours,
    SummerSchool,
    Funeral,
    HuntingSeason,
    MilitaryExercise,
  )

  case object External               extends FuzzyTimeType
  case object Dawn                   extends FuzzyTimeType
  case object Dusk                   extends FuzzyTimeType
  case object School                 extends FuzzyTimeType
  case object Holiday                extends FuzzyTimeType
  case object Winter                 extends FuzzyTimeType
  case object Spring                 extends FuzzyTimeType
  case object Summer                 extends FuzzyTimeType
  case object Autumn                 extends FuzzyTimeType
  case object HighTide               extends FuzzyTimeType
  case object LowTide                extends FuzzyTimeType
  case object HighWater              extends FuzzyTimeType
  case object LowWater               extends FuzzyTimeType
  case object Wet                    extends FuzzyTimeType
  case object Dry                    extends FuzzyTimeType
  case object PeakHours              extends FuzzyTimeType
  case object OffPeakHours           extends FuzzyTimeType
  case object Morning                extends FuzzyTimeType
  case object EveningRushHour        extends FuzzyTimeType
  case object MorningRushHour        extends FuzzyTimeType
  case object Day                    extends FuzzyTimeType
  case object Night                  extends FuzzyTimeType
  case object NonSchoolHours         extends FuzzyTimeType
  case object SchoolHours            extends FuzzyTimeType
  case object WhenChildrenArePresent extends FuzzyTimeType
  case object SunriseTillSunset      extends FuzzyTimeType
  case object SunsetTillSunrise      extends FuzzyTimeType
  case object Afternoon              extends FuzzyTimeType
  case object Event                  extends FuzzyTimeType
  case object MarketHours            extends FuzzyTimeType
  case object UndefinedOccasion      extends FuzzyTimeType
  case object RaceDays               extends FuzzyTimeType
  case object Pollution              extends FuzzyTimeType
  case object Evening                extends FuzzyTimeType
  case object BusinessHours          extends FuzzyTimeType
  case object SkiSeason              extends FuzzyTimeType
  case object TouristSeason          extends FuzzyTimeType
  case object ChurchHours            extends FuzzyTimeType
  case object SummerSchool           extends FuzzyTimeType
  case object Funeral                extends FuzzyTimeType
  case object HuntingSeason          extends FuzzyTimeType
  case object MilitaryExercise       extends FuzzyTimeType

}
