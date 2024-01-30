package com.intellias.osm.model.common

import com.intellias.osm.model.condition.FuzzyTimeType
import org.scalatest.funspec.AnyFunSpec
import com.intellias.osm.model.condition.TimeCondition.{DaysOfWeek, FuzzyTime, TimeRangeOfDay, TimeRangeOfWeekDays}
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

import java.time.{DayOfWeek, LocalTime}


class TimeConditionTest extends AnyFunSpec with Matchers {

  it("should serialize/deserialize to/from json"){
    val timeRangeOfDay = TimeRangeOfDay(LocalTime.MIN, LocalTime.MAX)
    Json.parse(Json.toJson(timeRangeOfDay).toString()).as[TimeRangeOfDay] shouldBe timeRangeOfDay

    val timeRangeOfWeekDays = TimeRangeOfWeekDays(Seq(DayOfWeek.MONDAY -> timeRangeOfDay, DayOfWeek.FRIDAY -> timeRangeOfDay))
    Json.parse(Json.toJson(timeRangeOfWeekDays).toString()).as[TimeRangeOfWeekDays] shouldBe timeRangeOfWeekDays

    val daysOfWeek = DaysOfWeek(Seq(DayOfWeek.MONDAY))
    Json.parse(Json.toJson(daysOfWeek).toString()).as[DaysOfWeek] shouldBe daysOfWeek

    val fuzzyTime = FuzzyTime(FuzzyTimeType.Afternoon)
    Json.parse(Json.toJson(fuzzyTime).toString()).as[FuzzyTime] shouldBe fuzzyTime
  }

}
