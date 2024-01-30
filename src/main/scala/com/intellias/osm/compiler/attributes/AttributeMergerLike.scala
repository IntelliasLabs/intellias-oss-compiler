package com.intellias.osm.compiler.attributes

import play.api.libs.json.{Reads, Writes}

trait AttributeMergerLike extends Serializable {
  type T

  implicit def writes: Writes[Seq[T]]
  implicit def reads: Reads[Seq[T]]

  def tag: String

  def merge(from: Seq[T], to: Seq[T]): Seq[T]
}
