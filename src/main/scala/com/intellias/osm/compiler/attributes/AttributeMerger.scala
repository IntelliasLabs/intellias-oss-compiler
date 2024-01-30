package com.intellias.osm.compiler.attributes

trait AttributeMerger[A] extends AttributeMergerLike {
  type T = A

  def merge(from: Seq[A], to: Seq[A]): Seq[A]
}
