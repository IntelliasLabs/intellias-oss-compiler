package org.apache.spark

import com.intellias.osm.model.admin.AdminType
import org.apache.spark.sql.types.{DataType, UserDefinedType}

class AdminTypeUDT extends UserDefinedType[AdminType]{
  override def sqlType: DataType = org.apache.spark.sql.types.StringType

  override def serialize(obj: AdminType): Any = obj.getClass.getSimpleName

  override def deserialize(datum: Any): AdminType = {
    AdminType.values.find(_.getClass.getSimpleName == datum) match {
      case Some(at) => at
      case None => throw new IllegalArgumentException(s"Unsupported AdminType: ${datum.getClass.getSimpleName}")
    }

  }

  override def userClass: Class[AdminType] = classOf[AdminType]
}
