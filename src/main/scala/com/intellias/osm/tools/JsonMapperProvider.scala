package com.intellias.osm.tools

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.reflect.{ClassTag, classTag}

trait JsonMapperProvider {
  @transient lazy val mapper: JsonMapper = JsonMapper.builder().addModule(DefaultScalaModule).build()

  def toJson(obj: Any): String = mapper.writeValueAsString(obj)
  def toJsonBytes(obj: Any): Array[Byte] = mapper.writeValueAsBytes(obj)
  def fromJson[A: ClassTag](objStr: String): A = {
    val clazz = classTag[A].runtimeClass.asInstanceOf[Class[A]]
    mapper.readValue(objStr, clazz)
  }
}
