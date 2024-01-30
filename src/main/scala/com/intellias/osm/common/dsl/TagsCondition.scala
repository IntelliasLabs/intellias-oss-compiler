package com.intellias.osm.common.dsl

import org.apache.commons.text.StringEscapeUtils

import scala.annotation.nowarn
import scala.util.parsing.combinator._

object TagsCondition extends Serializable {

  sealed trait Condition {
    def evaluate(data: Tags): Boolean
  }

  case class Equals(key: String, value: String) extends Condition {
    override def evaluate(data: Tags): Boolean =
      if (value.isEmpty) data.contains(key)
      else data.get(key).contains(StringEscapeUtils.unescapeJava(value))
  }

  case class Matches(keyRegex: String, valueRegex: String) extends Condition {
    override def evaluate(data: Tags): Boolean =
      if (valueRegex.isEmpty) data.exists { case (k, _) => k.matches(keyRegex) }
      else data.exists { case (k, v) => Option(k).exists(_.matches(keyRegex)) && Option(v).exists(_.matches(valueRegex)) }
  }

  case class And(left: Condition, right: Condition) extends Condition {
    override def evaluate(data: Tags): Boolean =
      left.evaluate(data) && right.evaluate(data)
  }

  case class Or(left: Condition, right: Condition) extends Condition {
    override def evaluate(data: Tags): Boolean =
      left.evaluate(data) || right.evaluate(data)
  }


  @nowarn
  object Parser extends RegexParsers {
    def matches: Parser[Matches] =
      ("'" ~> """([^'\\]|\\.)*""".r <~ "'") ~ "~" ~ ("'" ~> """([^'\\]|\\.)*""".r <~ "'") ^^ {
        case key ~ "~" ~ value =>
          Matches(key, value)
      }

    @nowarn
    def equals: Parser[Equals] =
      ("'" ~> """([^'\\]|\\.)*""".r <~ "'") ~ "=" ~ ("'" ~> """([^'\\]|\\.)*""".r <~ "'") ^^ {
        case key ~ "=" ~ value =>
          Equals(key, value)
      }

    def parenthesized: Parser[Condition] = "(" ~> OR <~ ")"

    def factor: Parser[Condition] = equals | matches | parenthesized

    def AND: Parser[Condition] = factor ~ rep("AND" ~> factor) ^^ {
      case base ~ list =>
        list.foldLeft(base)(And)
    }

    def OR: Parser[Condition] = AND ~ rep("OR" ~> AND) ^^ {
      case base ~ list =>
        list.foldLeft(base)(Or)
    }

    def apply(expression: String): Condition = {
      parseAll(OR, expression) match {
        case Success(result, _) => result
        case failure: NoSuccess => throw new IllegalArgumentException(s"Failed to parse expression:[${expression}], Error: ${failure.msg}")
      }
    }
  }
}