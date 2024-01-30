package com.intellias.osm

import org.apache.sedona.core.serde.SedonaKryoRegistrator
import org.apache.sedona.sql.utils. SedonaSQLRegistrator
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.SparkSession

trait SparkLocalRunner {
  implicit val spark: SparkSession = SparkSession
    .builder()
    .appName("Osm to NDS Live")
    .config("spark.master", "local[*]")
    .config("spark.executor.memory", "6g")
    .config("spark.sql.autoBroadcastJoinThreshold", 1000000)
    .config("spark.serializer", classOf[KryoSerializer].getName) // org.apache.spark.serializer.KryoSerializer
    .config("spark.kryo.registrator", classOf[SedonaKryoRegistrator].getName)
    .config("spark.local.dir", "/tmp/spark.local.dir/")
    .getOrCreate()

  SedonaSQLRegistrator.registerAll(spark)
}