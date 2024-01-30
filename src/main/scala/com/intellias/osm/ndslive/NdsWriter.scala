package com.intellias.osm.ndslive

import com.intellias.osm.NdsLiveConfig
import com.intellias.osm.common.{Processor, SharedProcessorData, StorageService}
import StorageService.Key
import org.apache.spark.sql.{Dataset, SparkSession}

import java.util.UUID


trait NdsWriter[R <: Product] extends Processor {
  def ndsConf: NdsLiveConfig
  def env: StorageService
  val failureEncoder: FailureEncoder[R] = new JsonFailureEncoder[R]()
  val failureKeyCreator: R => String = _ => s"${this.getClass.getSimpleName}-${UUID.randomUUID().toString}"

  override def process(data: SharedProcessorData)(implicit spark: SparkSession): SharedProcessorData = {
    val res: Dataset[Either[FailedResult[R], SuccessResult]] = convert(data)

    res.foreachPartition { iter: Iterator[Either[FailedResult[R], SuccessResult]] =>
      val (successes, failures) = iter.toSeq.foldLeft((Seq.empty[SuccessResult], Seq.empty[FailedResult[R]])) {
        case ((sAcc, fAcc), Right(s)) => (s +: sAcc, fAcc)
        case ((sAcc, fAcc), Left(f)) => (sAcc, f +: fAcc)
      }

      if(successes.nonEmpty) env.storageServices.foreach(_.save(successes.flatMap(_.payload)))
      if(failures.nonEmpty) env.storageServices.foreach(_.saveFailure(failures.map(toFailurePayload)))
    }

    data
  }

  def convert(data: SharedProcessorData)(implicit spark: SparkSession): Dataset[Either[FailedResult[R], SuccessResult]]

  def toFailurePayload(failed: FailedResult[R]): (Key, Array[Byte]) =
    (failureKeyCreator(failed.inputData), failureEncoder.encode(failed))

}
