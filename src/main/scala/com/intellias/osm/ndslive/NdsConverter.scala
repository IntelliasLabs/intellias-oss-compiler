package com.intellias.osm.ndslive

trait NdsConverter[S] {
  def convert(data: S): Either[FailedResult[S], SuccessResult]
}

case class FailedResult[T](message: String, inputData: T)
// Success result contains converted NDS result objects with their key.
case class SuccessResult(payload: Array[(String, Array[Byte])])
