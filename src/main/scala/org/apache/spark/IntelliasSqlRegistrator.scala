package org.apache.spark

import org.apache.spark.sql.types.UDTRegistration

object IntelliasSqlRegistrator {

  def registerAll(): Unit = {
    // NOTE: you have to put this file into the org.apache.spark package!
    UDTRegistration.register(classOf[AdminTypeUDT].getName, classOf[AdminTypeUDT].getName)
  }
}
