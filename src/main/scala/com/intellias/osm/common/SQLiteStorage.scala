package com.intellias.osm.common

import com.intellias.osm.StorageAPI
import com.intellias.osm.common.StorageService.{Key, Payload, Service}
import org.sqlite.SQLiteDataSource

class SQLiteStorage(val basePath: String, val failurePath: String) extends Service {
  private val regex = "(.*?)(?:_|-|Layer|Metadata|Meta)(?:_|Metadata|Meta)?(.*)".r
  private lazy val mapDb: Map[DbType, SQLiteDataSource] = {
    Seq(BaseType, FailureType).map { dbType =>
      val dataSource = new SQLiteDataSource()
      if (dbType == BaseType) {
        dataSource.setUrl(basePath)
      } else {
        dataSource.setUrl(failurePath)
      }
      dbType.asInstanceOf[DbType] -> dataSource
    }.toMap
  }

  override def save(key: Key, payload: Payload): Unit = {
    saveOne(key, payload, BaseType)
  }

  private def getRowInfoFromKey(key: Key) = {
    key match {
      case regex(layer, partition) => (layer, if (partition.isBlank) "meta" else partition)
      case _                       => throw new IllegalArgumentException(s"$key does not parsed as legal layer name")
    }
  }

  override def save(payloads: Iterable[(Key, Payload)]): Unit = {
    saveMultiple(payloads, BaseType)
  }

  private def saveMultiple(payloads: Iterable[(Key, Payload)], dbType: DbType): Unit = {
    val dataSource = mapDb(dbType)

    val data = payloads.map {
      case (key, bytes) =>
        val (table: Key, id: Key) = getRowInfoFromKey(key)
        (table, id, bytes)
    }.groupBy(_._1)
      .map {
        case (table, iter) =>
          val payloads = iter.map {
            case (_, id, bytes) => (id, bytes)
          }.toSeq
          (table, payloads)
      }
      .toSeq

    data
      .map(_._1)
      .distinct
      .foreach(table => {
        dataSource.getConnection.createStatement().execute(s"CREATE TABLE IF NOT EXISTS $table ( id TEXT, payload BLOB);)")
      })
    val connection = dataSource.getConnection

    data.foreach {
      case (table, seq) =>
        val updateSQL = s"INSERT INTO $table (id, payload) values (?, ?)"
        val statement = connection.prepareStatement(updateSQL)
        seq.foreach {
          case (id, bytes) =>
            statement.setString(1, id)
            statement.setBytes(2, bytes)
            statement.addBatch()
        }
        statement.executeBatch()
        statement.close()
    }
    connection.close()
  }

  override def saveFailure(key: Key, payload: Payload): Unit = {
    saveOne(key, payload, FailureType)
  }

  private def saveOne(key: Key, payload: Payload, dbType: DbType): Unit = {
    val dataSource = mapDb(dbType)

    val (table: Key, id: Key) = getRowInfoFromKey(key)

    dataSource.getConnection.createStatement().execute(s"CREATE TABLE IF NOT EXISTS $table ( id TEXT, payload BLOB);)")
    val updateSQL = s"INSERT INTO $table (id, payload) values (?, ?)"

    val connection = dataSource.getConnection
    val statement  = connection.prepareStatement(updateSQL)
    statement.setString(1, id)
    statement.setBytes(2, payload)
    statement.executeUpdate()
    statement.close()
    connection.close()
  }

  override def saveFailure(payloads: Iterable[(Key, Payload)]): Unit = {
    saveMultiple(payloads, FailureType)
  }

}
object SQLiteStorage {

  def apply(conf: StorageAPI): SQLiteStorage = {
    val basePath    = s"jdbc:sqlite:${conf.savePath}${java.io.File.separator}database.sq3"
    val failurePath = s"jdbc:sqlite:${conf.failurePath}${java.io.File.separator}failure_database.sq3"
    new SQLiteStorage(basePath, failurePath)
  }

}

trait DbType
case object BaseType    extends DbType
case object FailureType extends DbType
