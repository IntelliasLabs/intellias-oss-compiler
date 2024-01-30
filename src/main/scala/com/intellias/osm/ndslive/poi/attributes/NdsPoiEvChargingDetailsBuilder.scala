package com.intellias.osm.ndslive.poi.attributes

import com.intellias.osm.compiler.poi.attributes.PoiEvChargingDetailsExtractor
import com.intellias.osm.model.poi.{ElectricCurrentType, EvChargingConnector, POI, SocketType}
import nds.core.types.ElectricCurrent
import nds.poi.attributes.{PoiAttributeType, PoiAttributeValue}
import nds.poi.instantiations.{PoiAttribute, PoiAttributeMap}
import nds.poi.types.{EvChargingStationDetails, EvConnector, EvConnectorType}
import play.api.libs.json.Json

object NdsPoiEvChargingDetailsBuilder extends NdsPoiAttributeBuilder {
  override val attributeTypeCode: PoiAttributeType = PoiAttributeType.EV_CHARGING_DETAILS

  override def buildAttributes(pois: Array[POI]): Option[PoiAttributeMap] =
    buildAttrMap {
      pois.flatMap { poi =>
        poi.tags
          .get(PoiEvChargingDetailsExtractor.tag)
          .map(json => poi.ndsId -> Json.parse(json).as[Seq[EvChargingConnector]])
      }.map {
        case (poiId, evDetails) =>
          val poiAttribute = buildAttr(evDetails)
          (poiId, poiAttribute, emptyProperty, emptyCondition)
      }
    }

  private def buildAttr(evConnectors: Seq[EvChargingConnector]): PoiAttribute = {
    val connectors = evConnectors.map(toEvConnector)

    val evDetails = new EvChargingStationDetails()
    evDetails.setTotalPower(evConnectors.map(c => c.socketPower * c.countSockets).sum)
    evDetails.setNumConnectors(connectors.size)
    evDetails.setConnectors(connectors.toArray)
    evDetails.setChargers(evConnectors.map(_.countSockets).toArray)

    val attrVal = new PoiAttributeValue(attributeTypeCode)
    attrVal.setEvChargingStationDetails(evDetails)

    val attr = new PoiAttribute(attributeTypeCode)
    attr.setAttributeValue(attrVal)

    attr
  }

  private def toEvConnector(connector: EvChargingConnector): EvConnector = {
    val ev = new EvConnector()
    ev.setType(toEvConnectorType(connector.socketType))
    ev.setCurrent(toCurrent(connector.socketType.current))
    ev.setMaxPower(connector.socketPower)
    ev.setVoltage(connector.voltage)

    ev
  }

  private def toEvConnectorType(socketType: SocketType): EvConnectorType = socketType match {
    case SocketType.IEC62196_2_T2O => EvConnectorType.IEC62196_2_T2O
    case SocketType.COMBO_T2       => EvConnectorType.COMBO_T2
    case SocketType.DC_CHADEMO     => EvConnectorType.DC_CHADEMO
    case SocketType.DOMESTIC_F     => EvConnectorType.DOMESTIC_F
    case SocketType.IEC62196_2_T1C => EvConnectorType.IEC62196_2_T2O
  }

  private def toCurrent(current: ElectricCurrentType): ElectricCurrent = current match {
    case ElectricCurrentType.DC => ElectricCurrent.DC
    case ElectricCurrentType.AC => ElectricCurrent.AC
  }
}
