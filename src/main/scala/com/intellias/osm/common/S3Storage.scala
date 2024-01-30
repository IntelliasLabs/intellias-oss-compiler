package com.intellias.osm.common

import com.intellias.osm.common.StorageService.{Key, Payload, Service}
import com.intellias.osm.{AppConfig, StorageAPI}
import com.typesafe.scalalogging.StrictLogging
import pureconfig.generic.auto._
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

case class S3Storage(bucket: String, path: String, bucketFailures: String, pathFailures: String, credentials: S3Credentials)
    extends Service
    with StrictLogging {

  private lazy val s3Client: S3Client = {
    val awsCreds = StaticCredentialsProvider.create(AwsBasicCredentials.create(credentials.awsAccessKeyId, credentials.awsSecretAccessKey))
    S3Client
      .builder()
      .credentialsProvider(awsCreds)
      .region(Region.of(credentials.awsRegion))
      .build()
  }

  override def save(key: Key, payload: Payload): Unit = saveOne(key, payload, bucket, path)

  override def save(payloads: Iterable[(Key, Payload)]): Unit = saveMany(payloads, bucket, path)

  override def saveFailure(key: Key, payload: Payload): Unit = saveOne(key, payload, bucketFailures, pathFailures)

  override def saveFailure(payloads: Iterable[(Key, Payload)]): Unit = saveMany(payloads, bucketFailures, pathFailures)

  private def saveOne(key: Key, payload: Payload, bucket: String, path: String): Unit = {
    try {
      val putOb = PutObjectRequest
        .builder()
        .bucket(bucket)
        .key(path + key)
        .build()

      s3Client.putObject(putOb, RequestBody.fromBytes(payload))
    } catch {
      case e: Throwable => logger.error(s"Export to S3 is failed by reason: $e")
    }
  }

  private def saveMany(payloads: Iterable[(Key, Payload)], bucket: String, path: String): Unit = {
    payloads.foreach { case (key, payload) => saveOne(key, payload, bucket, path) }
  }
}

object S3Storage {
  private val regex = "s3://([^/,]+)(?:/(.*?))?$".r
  def apply(conf: StorageAPI): S3Storage = {

    val credentials: S3Credentials = AppConfig.read[S3Credentials](conf.credentialsPath)

    val (bucket, path) = conf.savePath match {
      case regex(s3bucket, s3path) => (s3bucket, s3path)
      case _                       => throw new IllegalArgumentException(s"${conf.savePath} does not parsed as S3 path")
    }

    val (bucketFailures, pathFailures) = conf.failurePath match {
      case regex(s3bucket, s3path) => (s3bucket, s3path)
      case _                       => throw new IllegalArgumentException(s"${conf.failurePath} does not parsed as S3 failure path")
    }

    new S3Storage(bucket, path, bucketFailures, pathFailures, credentials)
  }

}

case class S3Credentials(awsRegion: String, awsAccessKeyId: String, awsSecretAccessKey: String)
