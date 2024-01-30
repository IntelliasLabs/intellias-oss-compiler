package com.intellias.osm.common

import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession

import java.time.{Duration, LocalDateTime}

trait Workflow extends StrictLogging {

  def processors: Seq[Processor]

  def run(tables: SharedProcessorData = SharedProcessorData(tables = Map.empty))(implicit spark: SparkSession): SharedProcessorData = {
    val start = LocalDateTime.now()

    val res = processors.foldLeft(tables) {
      case (data, processor) => processor.process(data)
    }

    logger.info(s"${this.getClass.getName} processing time: ${Duration.between(start, LocalDateTime.now()).toSeconds} sec.")

    res
  }
}
