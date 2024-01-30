package com.intellias.osm.common

import org.apache.spark.sql.Dataset

case class SharedProcessorData(tables: Map[SourceType[_], Dataset[_]]) {
  def +[A](table: (SourceType[A], Dataset[A])): SharedProcessorData = this.copy(tables = tables + (table._1 -> table._2))
  def ++[A](newTables: Iterable[(SourceType[A], Dataset[A])]): SharedProcessorData = this.copy(tables = tables ++ newTables)
  def apply[A](key: SourceType[A]): Dataset[A] = tables(key).asInstanceOf[Dataset[A]]
  def get[A](key: SourceType[A]): Option[Dataset[A]] = tables.get(key).map(_.asInstanceOf[Dataset[A]])
}

trait SourceType[A]
