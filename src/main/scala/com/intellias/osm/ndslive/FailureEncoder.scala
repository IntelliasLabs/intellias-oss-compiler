package com.intellias.osm.ndslive

import com.intellias.osm.tools.JsonMapperProvider

trait FailureEncoder[R] extends Serializable {
  def encode(failed: FailedResult[R]): Array[Byte]
}

class JsonFailureEncoder[R] extends FailureEncoder[R] with JsonMapperProvider {
  override def encode(failed: FailedResult[R]): Array[Byte] = toJsonBytes(failed)
}
