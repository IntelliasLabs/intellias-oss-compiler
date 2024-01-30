package com.intellias.osm.compiler.display.attribute.value.area

import com.intellias.osm.compiler.display.attribute.SrcDisplayAreaAttribute
import play.api.libs.json._

sealed abstract class Color(val values: String*) extends SrcDisplayAreaAttribute

object Color {
  val colors: Set[_ <: Color] = Set(White, Grey, Brown, Red, Yellow, Beige, Black, Green, Orange)

  final case object White extends Color("white", "#ffffff")
  final case object Grey extends Color("grey", "gray", "#cccccc")
  final case object Brown extends Color("brown")
  final case object Red extends Color("red")
  final case object Yellow extends Color("yellow")
  final case object Beige extends Color("beige", "#eecfaf")
  final case object Black extends Color("black")
  final case object Green extends Color("green")
  final case object Orange extends Color("orange")
  final case object Unknown extends Color

  def lookup(value: String): Option[Color] = colors.find(color => color.values.contains(value.toLowerCase))
  def name(color: Color): String = color.values.headOption.getOrElse("")

  implicit val writes: Writes[Color] = Writes[Color] {
    color => Json.obj(("color", name(color)))
  }

  implicit val reads: Reads[Color] = Reads[Color] { json =>
    (json \ "color").validate[String]
      .flatMap(color => JsSuccess(lookup(color).getOrElse(Unknown)))
  }
}