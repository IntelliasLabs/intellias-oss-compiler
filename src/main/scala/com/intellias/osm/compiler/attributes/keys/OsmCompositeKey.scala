package com.intellias.osm.compiler.attributes.keys

import play.api.libs.json._

import scala.util.matching.Regex

trait OsmCompositeKey[T] {

  val values: Seq[T]

  def groupName: String

  def default: T

  def apply(m: Regex.Match): T = {
    val value = m.group(groupName)
    if (value == null) {
      default
    } else {
      apply(value)
    }
  }

  def apply(value: String): T = values.find(_.toString == value).getOrElse(default)

  // in case not all types used in values.
  val extraValuesForReaders: Seq[T] = Seq.empty

  implicit val compositeKeyWriters: Writes[T] = Writes[T] { keyType =>
    Json.obj("type" -> keyType.getClass.getSimpleName)
  }

  implicit val compositeKeyReaders: Reads[T] = Reads[T] { json =>
    (json \ "type").validate[String].flatMap { value =>
      (this.values ++ extraValuesForReaders).find(_.getClass.getSimpleName == value)
        .map(JsSuccess(_))
        .getOrElse(JsError(s"Unknown ${this.getClass.getName} type: $value"))
    }
  }
}

object OsmCompositeKey {

  def required[T](keyModifier: OsmCompositeKey[T]): (OsmCompositeKey[T], Boolean) = (keyModifier, true)

  def optional[T](keyModifier: OsmCompositeKey[T]): (OsmCompositeKey[T], Boolean) = (keyModifier, false)

  def flag(keyModifier: String): OsmCompositeKey[Boolean] = new OsmCompositeKey[Boolean] {
    override val values: Seq[Boolean] = Seq(true, false)

    override def groupName: String = keyModifier

    override def default: Boolean = false

    override def apply(value: String): Boolean = true
  }

  def requiredKey(group: String, key: String): OsmCompositeKey[String] = new OsmCompositeKey[String] {
    override val values: Seq[String] = Seq(key)

    override def groupName: String = group

    override def default: String = key

  }

  private def toModifiers(osmCompositeKeys: Seq[(OsmCompositeKey[?], Boolean)]): String =
    osmCompositeKeys.map {
      case (key, required) => s"(:(?<${key.groupName}>" + key.values.mkString("|") + "))" + (if (required) "" else "?")
    }.mkString

  def keyPattern(main: String, osmCompositeKeys: (OsmCompositeKey[?], Boolean)*): Regex = {
    val modifiers = toModifiers(osmCompositeKeys)

    s"^$main$modifiers$$".r
  }

  def keyPattern(main: OsmCompositeKey[?], osmCompositeKeys: (OsmCompositeKey[?], Boolean)*): Regex = {
    val modifiers = toModifiers(osmCompositeKeys)

    s"^(?<${main.groupName}>${main.values.mkString("|")})$modifiers$$".r
  }
}
