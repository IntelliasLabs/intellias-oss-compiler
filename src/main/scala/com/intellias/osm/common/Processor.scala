package com.intellias.osm.common

import org.apache.spark.sql.SparkSession

trait Processor extends Serializable {
  def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData
}




