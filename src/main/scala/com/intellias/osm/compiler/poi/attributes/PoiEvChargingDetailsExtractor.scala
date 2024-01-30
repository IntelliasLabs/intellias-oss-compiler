package com.intellias.osm.compiler.poi.attributes
import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey.{keyPattern, required}
import com.intellias.osm.compiler.attributes.keys.ValuesSeparator
import com.intellias.osm.compiler.poi.attributes.keys.OsmSocketType
import com.intellias.osm.model.poi.{EvChargingConnector, POI}
import play.api.libs.json.Writes

import scala.util.Try

object PoiEvChargingDetailsExtractor extends PoiAttributeExtractor[EvChargingConnector] {
  val tag = "NDS:EvChargingDetails"
  override implicit def writes: Writes[Seq[EvChargingConnector]] =  Writes.seq

  private val Kilowatt =  valueRegexp(Set("kW","kW","KW","kVA","KVA"))
  private val Amperes = valueRegexp(Set("A"))
  private val KilowattOrWatts = valueRegexp()
  private val voltageR = valueRegexp(Set("V"))

  private val SocketTypePattern = keyPattern(
    "socket",
    required(OsmSocketType)
  )


  def decodeFromOsm(poi: POI): Seq[EvChargingConnector] = toEvConnectorAndCount(poi.tags)

  private def toEvConnectorAndCount(tags: Map[String, String]): Seq[EvChargingConnector] = {
    tags.flatMap {
        case (key, rawValue) => SocketTypePattern.findFirstMatchIn(key).map(r => (OsmSocketType(r), key, rawValue))
    }.map {
      case (osmSocketType, key, countConnectors) =>
        val voltage = toVoltage(key, tags)
        val maxPower = toWats(key, tags, voltage)
        EvChargingConnector(
          socketType = osmSocketType.socketType,
          socketPower = maxPower,
          voltage = voltage,
          countSockets = Try(countConnectors.toShort).getOrElse(1)
        )
    }.toSeq
  }

  private def toWats(socketKey: String, tags: Map[String, String], voltage: Short): Int = {
    tags.get(s"$socketKey:output").map(a => a.split(ValuesSeparator).toSeq).map {
      _.map{
        case Kilowatt(k) =>
          k.toInt * 1000
        case Amperes(a) =>
          voltage * a.toInt
        case KilowattOrWatts(k) =>
          val kw = k.toInt
          if (kw < 1000) kw * 1000 else kw
        case _ => 1
      }.max
    }.getOrElse(1)
  }

  private def toVoltage(socketKey: String, tags: Map[String, String]): Short = {
    tags.get(s"$socketKey:voltage").map {
        case voltageR(v) => v.toInt
      }.getOrElse(400).toShort
  }

   private def valueRegexp(units: Set[String] = Set.empty) = ("^(?<value>\\d+)[ \t]*(?:" + units.mkString("|") + ")?" + "$").r

}
