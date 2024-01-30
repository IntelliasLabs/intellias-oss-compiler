package com.intellias.osm.common

import com.intellias.osm.common.StorageService.{Key, Payload, Service}
import com.intellias.osm.{StorageAPI, StorageConfig}

import java.nio.file.{Files, Paths}

trait StorageService {
  val storageServices: Seq[StorageService.Service]
}

object StorageService {
  type Key     = String
  type Payload = Array[Byte]

  trait Service extends Serializable {
    def save(key: Key, payload: Payload): Unit
    def save(payloads: Iterable[(Key, Payload)]): Unit
    def saveFailure(key: Key, payload: Payload): Unit
    def saveFailure(payloads: Iterable[(Key, Payload)]): Unit
  }

  def apply(config: StorageConfig): StorageService = new StorageService with Serializable {

    override val storageServices: Seq[Service] = config.api.map { api =>
      api.typeApi match {
        case "folder-config" => FileStorage(api)
        case "db-config" => SQLiteStorage(api)
        case "s3-config" => S3Storage(api)
        case _ => throw new IllegalArgumentException("unsupported storage type")
      }
    }
  }
}

class FileStorage(folderConf: StorageAPI) extends Service {
  override def save(key: Key, payload: Payload): Unit = {
    Files.write(Paths.get(folderConf.savePath, key), payload)
  }

  override def save(payloads: Iterable[(Key, Payload)]): Unit = payloads.foreach {
    case (key, bytes) => save(key, bytes)
  }

  override def saveFailure(key: Key, payload: Payload): Unit = {
    Files.write(Paths.get(folderConf.failurePath, key), payload)
  }

  override def saveFailure(payloads: Iterable[(Key, Payload)]): Unit = payloads.foreach {
    case (key, bytes) => saveFailure(key, bytes)
  }
}
object FileStorage {
  def apply(folderConf: StorageAPI): FileStorage = new FileStorage(folderConf)
}
