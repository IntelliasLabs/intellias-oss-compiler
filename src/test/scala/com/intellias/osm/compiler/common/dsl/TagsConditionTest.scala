package com.intellias.osm.compiler.common.dsl

import com.intellias.osm.common.dsl.TagsCondition.Parser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TagsConditionTest extends AnyFunSpec with Matchers{
  it("text dsl") {
    val data = Map("key1" -> "value1", "key2" -> "value2", "key3" -> "value3", "key4" -> "value4", "turn:left" -> "no", "highway:~1969" -> "no", "name" -> "McDonald's")

    val expr = Parser("('key1'='value1' AND 'key2'~'value.*') OR ('key3'='value3' AND 'key4'~'value4')")
    val expr2 = Parser("'key22'~'value.*' OR 'turn:left'~'noo'")
    val expr3 = Parser("'key2'~'.*' AND 'turn:left'~'no' AND 'key3'='value3'")
    val expr4 = Parser("'name'='McDonald\\'s'")
    val expr5 = Parser("'highway:~1969'='no'")
    val expr6 = Parser("'highway:~1969'=''")
    val expr7 = Parser("'highway.*'~''")

    expr.evaluate(data) shouldBe true
    expr2.evaluate(data) shouldBe false
    expr3.evaluate(data) shouldBe true
    expr4.evaluate(data) shouldBe true
    expr5.evaluate(data) shouldBe true
    expr6.evaluate(data) shouldBe true
    expr7.evaluate(data) shouldBe true
  }
}
