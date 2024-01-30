package com.intellias.osm.compiler.condition

import ch.poole.openinghoursparser.{OpeningHoursParser, Rule}
import com.intellias.osm.model.condition.FuzzyTimeType
import com.intellias.osm.model.condition.TimeCondition.{FuzzyTime, TimeRangeOfDay, TimeRangeOfWeekDays}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayInputStream
import java.time.{DayOfWeek, LocalTime}
import scala.jdk.CollectionConverters._
import scala.language.implicitConversions
import scala.util.{Failure, Success}

class OsmOpeningHoursToConditionSpec extends AnyFlatSpec with Matchers {

  import OsmOpeningHoursToCondition._

  it should "return an Failure for invalid opening hours string" in {
    val openingHoursStr = "Invalid opening hours string"
    val result = buildCondition(openingHoursStr)
    result shouldBe a[Failure[_]]
  }

  "buildCondition" should "return a sequence of valid conditions" in {
    val openingHoursStr = "Mo-Fr 12:30-13:30, 19:00-19:30"

    val result = buildCondition(openingHoursStr)
    result shouldBe a[Success[_]]
    result.get.size shouldBe 2
  }

  "toTimeRangeOfDay" should "return a sequence of conditions for the given time" in {
    val rule = parseTime("19:00-19:30").head
    val expectedConditions = Seq(TimeRangeOfDay(LocalTime.of(19, 0), LocalTime.of(19, 30)))
    val result = toTimeOfDay(rule.getTimes.asScala)
    result should be (expectedConditions)
  }

  "toTimeRangeOfWeekDays" should "return a sequence of conditions for the given days-times" in {
    val rule = parseTime("Mo-Tu 12:30-13:30").head

    val time = TimeRangeOfDay(LocalTime.of(12, 30), LocalTime.of(13, 30))
    val expectedConditions = TimeRangeOfWeekDays(days = Seq(
      DayOfWeek.MONDAY -> time,
      DayOfWeek.TUESDAY -> time
    ))

    val result = toTimeRangeOfWeekDays(rule.getDays.asScala, rule.getTimes.asScala)
    result shouldBe Seq(expectedConditions)
  }

  "toTimeOfDay" should "return a sequence of TimeRangeOfDay objects for the given times" in {
    val rule = parseTime("19:00-19:30").head

    val expectedTimeRangeOfDay = TimeRangeOfDay(LocalTime.of(19, 0), LocalTime.of(19, 30))

    val result =  toTimeOfDay(rule.getTimes.asScala)
    result shouldBe Seq(expectedTimeRangeOfDay)
  }

  "toFuzzyTimeRangeOfDay" should "return a sequence of FuzzyTime objects for the given times" in {
    val rule = parseTime("sunset-sunrise").head

    val expectedTimeRangeOfDay = FuzzyTime(fuzzyTimeType = FuzzyTimeType.SunsetTillSunrise)

    val result = toFuzzyTimeRangeOfDay(rule.getTimes.asScala).toSeq
    result shouldBe Seq(expectedTimeRangeOfDay)
  }

  "convertMinutes" should "return a tuple of hours and minutes for the given minutes from midnight" in {
    val result = convertMinutes(750)
    result should be((12, 30))
  }


  private def parseTime(str: String): Iterable[Rule] = {
    val parser = new OpeningHoursParser(new ByteArrayInputStream(str.getBytes()))
    parser.rules(true, true).asScala
  }
}
